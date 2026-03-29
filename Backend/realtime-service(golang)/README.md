<div align="center">

# ⚡ Realtime Service

**Trạm điều phối WebSocket — Đưa tin nhắn đến tay người dùng trong milliseconds.**

![Go](https://img.shields.io/badge/Go-1.25-00ADD8?style=for-the-badge&logo=go&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-gorilla/websocket-00ADD8?style=for-the-badge)
![Redis](https://img.shields.io/badge/Redis-Pub/Sub-DC382D?style=for-the-badge&logo=redis&logoColor=white)

</div>

---

## 📋 Tổng Quan

**Realtime Service** là service đặc biệt nhất trong hệ thống — nó không lưu bất kỳ dữ liệu nào vào database. Nhiệm vụ duy nhất và tối cao của nó là **duy trì hàng nghìn kết nối WebSocket đồng thời** và **đưa tin nhắn/sự kiện đến đúng người trong thời gian thực**.

Được viết bằng **Golang** — ngôn ngữ được sinh ra để xử lý concurrency cao — service này hoạt động như một "trạm điều phối" cực kỳ nhẹ và nhanh. Nó lắng nghe Redis Pub/Sub và phân phát lại xuống các WebSocket client tương ứng.

### Tại sao chọn Go cho service này?

| Tiêu chí | Go | Node.js | Java |
|---|---|---|---|
| Concurrency model | **Goroutines** (1-2KB/goroutine) | Event loop đơn luồng | Threads (1MB+/thread) |
| 10k kết nối đồng thời | ~20MB RAM | ~200MB RAM | ~10GB RAM |
| Latency | Sub-millisecond | Low | Medium |
| Compile | Static binary | Cần Node.js runtime | Cần JVM |

Với mỗi user kết nối WebSocket, service spawn 2 goroutines nhẹ nhàng (ReadPump + WritePump). 10,000 users đồng thời = 20,000 goroutines = vẫn chỉ tốn vài chục MB RAM.

---

## ✨ Tính Năng Cốt Lõi

### 1. 🔌 WebSocket Connection Management

Mỗi khi Frontend mở tab chat, nó kết nối WebSocket:
```
ws://localhost:3004/ws?userId={userId}&channelId={channelId}
```

Service tạo ra một `Client` object đại diện cho kết nối đó và đăng ký vào `Manager` trung tâm.

### 2. 🏠 Room-based Broadcasting (Phát sóng theo phòng)

Mô hình "phòng chat" (Room) được implement thuần túy bằng **Go in-memory maps**:

```go
type Manager struct {
    Clients   map[string]*Client               // userId → Client
    Rooms     map[string]map[string]*Client    // channelId → {userId → Client}
    // ...
}
```

Khi tin nhắn mới đến, service chỉ push đến những client **đang ở trong đúng channelId đó**, không broadcast toàn bộ. Đây là thiết kế tối ưu cho hệ thống có hàng nghìn kênh.

### 3. 📡 Redis Pub/Sub Integration

Đây là "cầu nối" giữa Chat Service và các trình duyệt:

```
Chat Service
    │ PUBLISH "ChatService_channel:abc123" 
    │ { channelId: "abc123", userId: "...", content: "Hello" }
    ▼
Redis Pub/Sub
    │ PSubscribe "ChatService_channel:*"  ← Pattern subscribe, bắt TẤT CẢ channels
    ▼
Realtime Service (Go)
    │ Nhận message từ Redis
    │ Tìm clients trong Room "abc123"
    │ Gửi cho từng client (NGOẠI TRỪ người gửi)
    ▼
Các trình duyệt nhận được tin nhắn tức thì
```

Service dùng **Pattern Subscribe** (`PSubscribe "ChatService_channel:*"`) thay vì subscribe từng channel riêng lẻ. Điều này có nghĩa là service tự động nhận sự kiện từ **mọi channel** mà không cần biết trước danh sách.

### 4. 💚 Presence System (Trạng thái Online/Offline)

Khi user kết nối WebSocket:
1. Ghi `presence:{userId} = "online"` vào Redis với **TTL 5 phút** (tự động hết hạn — phòng "online ảo")
2. Broadcast sự kiện `USER_STATUS: online` đến tất cả clients khác

Khi user ngắt kết nối (đóng tab, mất mạng):
1. Xóa key `presence:{userId}` khỏi Redis
2. Broadcast `USER_STATUS: offline`

```go
// Ghi với TTL để chống "online ảo" khi server crash
m.RedisClient.Set(ctx, "presence:"+userId, "online", 5*time.Minute)
```

### 5. 🎮 Client Command Router

Frontend có thể gửi các lệnh lên server qua WebSocket (JSON-based protocol):

```json
// Tham gia phòng chat
{ "action": "join", "room": "channel-uuid" }

// Rời phòng chat
{ "action": "leave", "room": "channel-uuid" }

// Báo đang gõ phím (Đã được thiết kế sẵn, chưa fully implement)
{ "action": "typing", "room": "channel-uuid", "isTyping": true }

// Đánh dấu đã đọc (Được thiết kế sẵn để implement sau)
{ "action": "mark_read", "messageId": "message-uuid" }
```

Architecture cho phép mở rộng thêm action mới cực kỳ dễ dàng:
```go
switch command.Action {
    case "join":    ...
    case "leave":   ...
    case "typing":  ...  // Cắm vào đây là xong
    // Thêm case mới ở đây không ảnh hưởng code cũ
}
```

### 6. 🏓 Heartbeat (Giữ kết nối sống)

Service tự động gửi `Ping` frame xuống client mỗi **20 giây**. Nếu client không trả lời `Pong` trong **25 giây**, kết nối bị coi là "chết" và được dọn dẹp tự động. Điều này giải quyết bài toán "ghost connections" — kết nối bị treo mà server không biết.

```go
const (
    pongWait   = 25 * time.Second  // Đợi Pong tối đa
    pingPeriod = 20 * time.Second  // Gửi Ping mỗi 20s
    writeWait  = 10 * time.Second  // Timeout ghi
    maxMessageSize = 4096          // Max 4KB mỗi message
)
```

---

## 🏗️ Kiến Trúc Bên Trong

```
┌─────────────────────────────────────────────────┐
│              Realtime Service (Go)               │
│                                                 │
│  ┌─────────────────────────────────────────┐   │
│  │            Manager (Singleton)          │   │
│  │                                         │   │
│  │  Channels:                              │   │
│  │  ├── Register    chan *Client           │   │
│  │  ├── Unregister  chan *Client           │   │
│  │  ├── JoinRoom    chan *RoomAction       │   │
│  │  ├── LeaveRoom   chan *RoomAction       │   │
│  │  └── Broadcast   chan []byte            │   │
│  │                                         │   │
│  │  func Run() { select { ... } }          │   │  ← Goroutine đơn, xử lý
│  └─────────────────────────────────────────┘   │    tuần tự, KHÔNG race
│                                                 │
│  ┌───────────────────────────────────────────┐ │
│  │  Client 1 (User A - Tab Chrome)           │ │
│  │  ReadPump()  → Goroutine đọc từ browser   │ │
│  │  WritePump() → Goroutine ghi xuống browser│ │
│  └───────────────────────────────────────────┘ │
│                  ...                            │
│  ┌───────────────────────────────────────────┐ │
│  │  Client N (User Z - Tab Firefox)          │ │
│  └───────────────────────────────────────────┘ │
│                                                 │
│  ┌───────────────────────────────────────────┐ │
│  │  listenRedis() Goroutine                  │ │
│  │  PSubscribe "ChatService_channel:*"       │ │
│  │  → Nhận message → Manager.Broadcast ← ch │ │
│  └───────────────────────────────────────────┘ │
└─────────────────────────────────────────────────┘
```

### Tại sao Manager lại dùng `select` thay vì Mutex?

Go channels được dùng thay cho Mutex/Lock để đồng bộ hóa state. Tất cả thay đổi state của Manager (thêm client, xóa client, join room...) đều đi qua một **goroutine Run() duy nhất**. Cách này:
- Không có race condition (concurrency safe by design)
- Không có deadlock (không mutex)
- Code dễ đọc và reason về

---

## 📁 Cấu Trúc File

```
realtime-service(golang)/
├── cmd/
│   └── server/
│       └── main.go                # Entry point: Khởi động HTTP server, Redis, Manager
│
├── internal/
│   ├── config/                    # Đọc config từ .env
│   ├── infrastructure/            # Khởi tạo Redis client
│   └── websocket/
│       ├── client.go              # Client struct: ReadPump, WritePump, Command Router
│       ├── manager.go             # Manager struct: State management, Run() loop
│       ├── manager_handlers.go    # Handle Register, Unregister, JoinRoom, LeaveRoom, Broadcast
│       └── manager_redis.go       # updatePresence (Redis), notifyStatusChange (broadcast)
│
├── pkg/                           # Shared utilities
├── go.mod                         # Dependencies
├── go.sum
└── .env                           # Environment variables
```

---

## 🔗 Kết Nối WebSocket từ Frontend (ví dụ)

```javascript
// Frontend JavaScript
const userId = getCurrentUserId();
const channelId = getCurrentChannelId();

const ws = new WebSocket(`ws://localhost/ws/realtime/ws?userId=${userId}&channelId=${channelId}`);

ws.onmessage = (event) => {
    const data = JSON.parse(event.data);
    
    if (data.type === "USER_STATUS") {
        updateOnlineStatus(data.userId, data.status); // online/offline
    } else {
        // Đây là tin nhắn mới từ Chat Service
        appendMessage(data);
    }
};

// Tham gia channel chat room
ws.send(JSON.stringify({ action: "join", room: channelId }));
```

---

## ⚙️ Cài Đặt & Chạy

### Điều kiện
- Go 1.21 trở lên
- Redis đang chạy (từ `docker-compose up -d`)

```bash
cd realtime-service(golang)

# Chạy trực tiếp (Development)
go run cmd/server/main.go

# Build binary production
go build -o realtime-service cmd/server/main.go
./realtime-service
```

Service khởi động tại: `http://localhost:3004`
WebSocket endpoint: `ws://localhost:3004/ws`

### Biến Môi Trường (`.env`)
```env
SERVER_PORT=3004
REDIS_ADDR=localhost:6379
```

---

## 🔍 Điểm Nổi Bật Kỹ Thuật

- **Zero Database**: Service này không cần database — 100% in-memory + Redis. Khởi động trong ~50ms
- **Goroutine per Connection**: Mỗi kết nối = 2 goroutines nhẹ, không cần thread pool phức tạp
- **Pattern Subscribe**: Một subscription để bắt tất cả channels — không cần re-subscribe khi có channel mới
- **Non-blocking Broadcast**: Nếu một client bị "nghẽn" (slow consumer), server không block; thay vào đó tự disconnect client đó để bảo vệ hàng đợi
- **Anti-echo**: Người gửi tin nhắn không nhận lại tin nhắn của chính mình qua WebSocket (tránh duplicate trên UI)
- **Graceful Cleanup**: Khi client disconnect, tất cả rooms và channels liên quan được dọn dẹp hoàn toàn, tránh memory leak
