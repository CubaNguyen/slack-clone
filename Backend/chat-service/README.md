<div align="center">

# 💬 Chat Service

**Engine xử lý tin nhắn hiệu suất cao — Được xây dựng theo kiến trúc Clean Architecture với CQRS và MediatR.**

![.NET](https://img.shields.io/badge/.NET-9.0-512BD4?style=for-the-badge&logo=dotnet&logoColor=white)
![C#](https://img.shields.io/badge/C%23-13-239120?style=for-the-badge&logo=csharp&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-PubSub-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-Consumer-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)

</div>

---

## 📋 Tổng Quan

**Chat Service** là service đảm nhận toàn bộ nghiệp vụ nhắn tin: lưu trữ, truy xuất, và xử lý tin nhắn trong các kênh (channels). Sau khi lưu tin nhắn thành công, service kích hoạt luồng **real-time** bằng cách publish sự kiện lên **Redis Pub/Sub**, từ đó Realtime Service (Go) sẽ đẩy ngay xuống trình duyệt của tất cả thành viên trong kênh.

Được xây dựng theo **Clean Architecture** — một trong những mô hình kiến trúc phần mềm được tôn trọng nhất trong cộng đồng .NET — đảm bảo code dễ test, dễ mở rộng và tách biệt rõ ràng giữa các tầng.

---

## ✨ Tính Năng Cốt Lõi

### 1. 📨 Hệ Thống Tin Nhắn Đầy Đủ

| API | Method | Mô tả |
|---|---|---|
| `POST /api/messages` | Gửi tin nhắn mới | Lưu DB + Publish Redis (kích hoạt real-time) |
| `GET /api/messages/{channelId}` | Lấy lịch sử tin nhắn | **Cursor-based pagination** (không dùng offset) |
| `PUT /api/messages/{channelId}/messages/{messageId}` | Sửa tin nhắn | Chỉ người viết mới được sửa |
| `DELETE /api/messages/{channelId}/messages/{messageId}` | Xóa tin nhắn | **Soft delete** (đánh dấu deleted, không xóa khỏi DB) |

### 2. 🧵 Hệ Thống Thread (Luồng thảo luận)

Hỗ trợ mô hình nhiều cấp: **Tin nhắn → Thread → Reply**. Đây là tính năng đặc trưng của Slack góp phần giữ cho cuộc trò chuyện luôn có tổ chức:

```
GET /api/messages/{channelId}/threads/{parentId}
```

Response trả về một `ThreadDetailDto` gồm:
- **Tin nhắn gốc** (Parent Message) với đầy đủ nội dung
- **Danh sách tất cả replies** được sắp xếp theo thời gian
- **Metadata thống kê** (total replies, latest reply time...)

Khi có reply mới trong một thread, service tự động cập nhật `reply_count` và `latest_reply_at` trên tin nhắn gốc thông qua **Background Consumer**.

### 3. 😊 Reaction System (Cảm xúc trên tin nhắn)

```
POST /api/messages/{channelId}/messages/{messageId}/reactions
Body: ":heart:"
```

Logic **Toggle** thông minh:
- Nếu người dùng **chưa thả** emoji đó: Thêm reaction
- Nếu người dùng **đã thả** emoji đó rồi: Rút lại (toggleable)

API trả về `true` (đã thả) hoặc `false` (đã rút lại).

### 4. 📌 Pin Messages (Ghim tin nhắn)

```
POST /api/messages/{channelId}/messages/{messageId}/pin  → Toggle ghim/bỏ ghim
GET  /api/messages/{channelId}/pins                       → Lấy danh sách đã ghim
```

Cho phép Admin/Member ghim những tin nhắn quan trọng để team dễ tìm lại.

### 5. ⚡ Real-time Bridge qua Redis Pub/Sub

Khi một tin nhắn mới được gửi, Chat Service không tự xử lý WebSocket. Thay vào đó, nó **publish một event lên Redis channel** với pattern `ChatService_channel:{channelId}`:

```
Chat Service               Redis Pub/Sub              Realtime Service (Go)
     │                                                        │
     │── Lưu tin nhắn vào DB ──────────────────────────────── │
     │                                                        │
     │── PUBLISH "ChatService_channel:abc123" ──────────────► │
     │   { channelId, userId, content, ... }                  │
     │                                                        │  
     │                                               ◄─────── │ Tìm tất cả WebSocket
     │                                                        │ clients trong room "abc123"
     │                                                        │ → Push message xuống trình duyệt
```

Kiến trúc này đảm bảo Chat Service **không biết** và **không cần quan tâm** đến WebSocket — giữ đúng nguyên tắc Single Responsibility.

### 6. 🎯 Channel Authorization (Phân quyền truy cập kênh)

Mỗi Message Command/Query đi qua **MediatR Pipeline Behavior** — một middleware tự động kiểm tra:
- User hiện tại có phải là thành viên của channel không?
- Nếu không: Trả về lỗi ngay, không cho phép đọc/ghi tin nhắn

```csharp
// Khai báo trong Program.cs
builder.Services.AddMediatR(cfg => {
    cfg.AddBehavior(typeof(IPipelineBehavior<,>), typeof(ChannelAuthorizationBehavior<,>));
});
```

Cách này đảm bảo **100% các operations** đều được kiểm tra phân quyền mà không cần dev phải nhớ thêm check vào từng handler.

---

## 🏛️ Kiến Trúc Clean Architecture

```
chat-service/
│
├── Domain/                        # 🧠 Thuần túy nghiệp vụ — Không bergabung vào bất cứ framework nào
│   ├── Entities/                  #    Message, Channel, Reaction, Pin Aggregates
│   ├── Common/                    #    Base Entity, Domain Event interface
│   ├── Exceptions/                #    Domain exceptions (MessageNotFoundException, UnauthorizedException...)
│   ├── enum/                      #    MessageType (TEXT, FILE, IMAGE), ChannelType...
│   └── Constants/                 #    Constants dùng trong domain
│
├── Application/                   # 🎯 Điều phối nghiệp vụ (CQRS với MediatR)
│   ├── Messages/
│   │   ├── Commands/              #    Write Operations
│   │   │   ├── SendMessage/       #      SendMessageCommand + Handler
│   │   │   ├── EditMessage/       #      EditMessageCommand + Handler
│   │   │   ├── DeleteMessage/     #      DeleteMessageCommand + Handler (soft delete)
│   │   │   ├── ToggleReaction/    #      ToggleReactionCommand + Handler
│   │   │   └── TogglePin/        #      TogglePinCommand + Handler
│   │   └── Queries/               #    Read Operations
│   │       ├── GetMessages/       #      Cursor-based pagination query
│   │       ├── GetThreadMessages/ #      Thread detail query
│   │       └── GetPinnedMessages/ #      Get pinned messages query
│   ├── Channels/                  #    Channel-level commands
│   └── Common/
│       ├── Interfaces/            #    IApplicationDbContext, IRedisBusService, IKafkaProducer...
│       └── Models/                #    Shared DTOs, ApiResponse wrapper
│
├── Infrastructure/                # 🔧 Kết nối với thế giới bên ngoài
│   ├── Persistence/               #    EF Core DbContext, Repositories, Migrations
│   ├── Redis/                     #    Redis connection + RedisBusService (Pub/Sub)
│   ├── Kafka/
│   │   ├── Services/              #    KafkaProducer (gửi event đi)
│   │   └── Consumers/             #    Background Consumers (nhận event từ Workspace)
│   │       ├── ChannelReadBatchConsumer  #  Đồng bộ channel permissions
│   │       └── ThreadMetadataConsumer   #  Cập nhật thread metadata
│   ├── Middleware/
│   │   └── GlobalExceptionMiddleware   #  Xử lý toàn bộ exception thành JSON chuẩn
│   └── Services/
│       └── UserContext            #    Đọc x-user-id từ Header (inject bởi Gateway)
│
└── Presentation/                  # 🌐 API Layer
    ├── Controllers/
    │   ├── MessagesController.cs  #    Message CRUD + Reactions + Pins + Threads
    │   └── ChannelsController.cs  #    Channel-level operations
    ├── Hubs/                      #    (Dự phòng cho SignalR nếu cần)
    └── Middlewares/               #    Custom middleware

```

---

## 🗄️ Schema Database Chính

### Bảng `messages`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | UUID PK | |
| `channel_id` | UUID | Kênh chứa tin nhắn |
| `user_id` | UUID | Người gửi (từ Gateway Header) |
| `content` | TEXT | Nội dung tin nhắn |
| `type` | ENUM | `TEXT` / `FILE` / `IMAGE` / `SYSTEM` |
| `parent_id` | UUID NULL | ID tin nhắn gốc nếu là reply trong Thread |
| `reply_count` | INT | Số lượng reply trong thread |
| `latest_reply_at` | TIMESTAMP NULL | Thời gian reply cuối cùng |
| `is_deleted` | BOOLEAN | Soft delete flag |
| `is_pinned` | BOOLEAN | Đã được ghim chưa |
| `created_at` | TIMESTAMP | |
| `updated_at` | TIMESTAMP | |

### Bảng `reactions`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | UUID PK | |
| `message_id` | UUID FK | |
| `user_id` | UUID | Người thả reaction |
| `emoji` | VARCHAR | Ví dụ: `:heart:`, `:thumbsup:` |

### Bảng `channel_members` (Sync từ Workspace)
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | UUID PK | |
| `channel_id` | UUID | |
| `user_id` | UUID | |
| `joined_at` | TIMESTAMP | |

*Bảng này được Kafka Consumer tự động đồng bộ từ Workspace Service khi có thành viên được thêm/xóa.*

---

## 📊 Cursor-Based Pagination

Thay vì dùng `OFFSET/LIMIT` (sẽ chậm khi data lớn), chat service dùng **Cursor Pagination**:

```
GET /api/messages/{channelId}?before=2026-03-28T10:00:00Z&limit=50
```

Cách hoạt động:
```sql
SELECT * FROM messages
WHERE channel_id = @channelId
  AND created_at < @before    -- "trước cursor này"
ORDER BY created_at DESC
LIMIT @limit
```

Lợi ích: Hiệu suất không đổi dù database có hàng triệu tin nhắn. Đây là cách Slack, Discord thực sự implement.

---

## 🔧 Background Consumers

Service có **2 Background Consumer** chạy ngầm khi khởi động, lắng nghe Kafka và xử lý bất đồng bộ:

### `ChannelReadBatchConsumer`
- Lắng nghe Kafka topic: `workspace-events`
- Nhận event `MEMBER_JOINED` / `MEMBER_LEFT` từ Workspace Service
- Cập nhật bảng `channel_members` để đồng bộ quyền đọc tin nhắn

### `ThreadMetadataConsumer`
- Lắng nghe sự kiện khi có reply mới trong thread
- Cập nhật `reply_count` và `latest_reply_at` trên tin nhắn gốc
- Giữ metadata luôn nhất quán mà không block main flow

---

## ⚙️ Cài Đặt & Chạy

### Điều kiện
- .NET 9.0 SDK
- PostgreSQL (Database: `slack_chat`)
- Redis, Kafka đang chạy

```bash
cd chat-service

# Chạy development
dotnet run

# Build production
dotnet publish -c Release
```

Service khởi động tại: `http://localhost:3003`

### Cấu hình (`appsettings.json`)
```json
{
  "ConnectionStrings": {
    "DefaultConnection": "Host=localhost;Port=5432;Database=slack_chat;Username=postgres;Password=YOUR_PASSWORD"
  },
  "Kafka": {
    "BootstrapServers": "localhost:9092",
    "GroupId": "chat-service-consumer-group",
    "Topic": "workspace-events"
  },
  "Redis": {
    "Configuration": "localhost:6379",
    "InstanceName": "ChatService_"
  }
}
```

---

## 📖 Swagger UI

Khi service đang chạy:
```
http://localhost:3003/swagger
```

Mọi endpoint đều có XML documentation đầy đủ (summary, param description, response codes).

---

## 🔍 Điểm Nổi Bật Kỹ Thuật

- **MediatR Pipeline Behavior**: Authorization được áp dụng tự động cho toàn bộ operations mà không cần decorator hay manual check
- **Global Exception Middleware**: Toàn bộ exception (Domain, Infrastructure, Runtime) được bắt tại một điểm và trả về JSON response nhất quán
- **Soft Delete**: Tin nhắn bị xóa không bao giờ biến mất khỏi DB, chỉ bị đánh dấu — đảm bảo thread history, audit trail
- **Redis as Bridge**: Decoupling hoàn toàn giữa storage logic và real-time delivery — Chat Service không biết WebSocket tồn tại
