<div align="center">

# 🛡️ API Gateway — YARP Reverse Proxy

**Cổng bảo vệ duy nhất của toàn bộ hệ thống Slack Clone.**

![.NET](https://img.shields.io/badge/.NET-9.0-512BD4?style=for-the-badge&logo=dotnet&logoColor=white)
![YARP](https://img.shields.io/badge/YARP-Reverse_Proxy-0078D4?style=for-the-badge)
![JWT](https://img.shields.io/badge/JWT-Authentication-F7B731?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

</div>

---

## 📋 Tổng Quan

**API Gateway** là điểm chạm duy nhất (Single Entry Point) giữa thế giới bên ngoài (Browser, Mobile App) và hệ thống microservices nội bộ. Không một request nào từ phía client được phép đi trực tiếp vào bất kỳ service nào — tất cả đều phải đi qua cổng bảo vệ này.

Service được xây dựng trên nền tảng **YARP (Yet Another Reverse Proxy)** — thư viện Reverse Proxy mã nguồn mở hiệu năng cao của Microsoft, tích hợp native vào ASP.NET Core.

---

## ✨ Tính Năng Cốt Lõi

### 1. 🔀 Intelligent Routing (Định tuyến thông minh)

Toàn bộ quy tắc định tuyến được khai báo **100% bằng JSON** trong `appsettings.json`, không cần viết code controller:

| Prefix URL đến | Chuyển tiếp đến | Mô tả |
|---|---|---|
| `/api/identity/public/*` | `Identity Service :3001` | Đăng nhập, Đăng ký (Có Rate Limit) |
| `/api/identity/secure/*` | `Identity Service :3001` | Quản lý tài khoản (Cần Auth) |
| `/api/ws/*` | `Workspace Service :3002` | Quản lý Workspace & Channel |
| `/api/chat/*` | `Chat Service :3003` | Gửi & nhận tin nhắn |
| `/ws/realtime/*` | `Realtime Service :3004` | WebSocket kết nối thời gian thực |
| `/api/mobile-bff/*` | `Slack BFF :3300` | API tổng hợp dành riêng cho Mobile |

### 2. 🛡️ JWT Authentication & Authorization

Gateway xác thực **tập trung** cho toàn bộ hệ thống. Các downstream service **không cần implement JWT lại**:

```
Client gửi: Authorization: Bearer <JWT_TOKEN>
     ↓
Gateway xác thực chữ ký bằng SecretKey
     ↓
Nếu hợp lệ → Bóc tách Claims (user_id, email)
     ↓
Inject vào Header: x-user-id, x-user-email
     ↓
Forward xuống Downstream Service
```

Downstream service chỉ cần đọc header `x-user-id` là biết đang phục vụ ai — không cần hiểu JWT.

### 3. ⏳ Rate Limiting (Bảo vệ chống spam/DDoS)

Áp dụng **Fixed Window Rate Limiter** để bảo vệ các endpoint nhạy cảm:

- **Chiến lược**: `StrictPolicy` — Tối đa **5 request / 10 giây**
- **Áp dụng cho**: Login, Register, Forgot Password, và Chat API
- **Khi vượt ngưỡng**: Trả về `429 Too Many Requests` ngay lập tức, không đợi

### 4. 🔧 Header Injection Transform

Đây là tính năng thông minh nhất của Gateway. Khi một request đã được xác thực:

```csharp
// Gateway tự động thêm 2 headers này vào mỗi request chuyển tiếp
transformContext.ProxyRequest.Headers.Add("x-user-id", userId);
transformContext.ProxyRequest.Headers.Add("x-user-email", email);
```

Điều này đảm bảo **mọi service nội bộ** luôn biết chính xác user nào đang thao tác mà không cần phải tự decode JWT.

### 5. 🌐 CORS Management

Quản lý tập trung Cross-Origin Resource Sharing, cho phép các domain dev chuẩn:
- `http://localhost:3000` (React / Next.js thông thường)
- `http://localhost:5173` (Vite dev server)

---

## 🏗️ Kiến Trúc Bên Trong

```
Request từ Client
      │
      ▼
┌─────────────────────────────────────────┐
│            ASP.NET Core Pipeline        │
│                                         │
│  1. CORS Middleware                     │
│  2. JWT Authentication Middleware       │
│  3. Authorization Middleware            │
│  4. Rate Limiter Middleware             │
│                                         │
│  ┌──────────────────────────────────┐   │
│  │      YARP Reverse Proxy Engine  │   │
│  │  - Route Matching               │   │
│  │  - Header Injection Transform   │   │
│  │  - Forward to Cluster           │   │
│  └──────────────────────────────────┘   │
└─────────────────────────────────────────┘
      │
      ▼
 Downstream Microservice
```

---

## ⚙️ Cấu Hình & Chạy

### Yêu Cầu
- .NET 9.0 SDK trở lên

### Cài đặt & Chạy
```bash
cd api-gateway/YarpGateway
dotnet run
```

### Cấu Hình JWT (appsettings.json)
```json
{
  "JwtSettings": {
    "SecretKey": "your-secret-key-here",
    "Issuer": "SlackCloneIdentity",
    "Audience": "SlackCloneApp"
  }
}
```

> ⚠️ **Quan trọng**: `SecretKey` phải giống hệt với key được dùng trong **Identity Service** để ký JWT. Nếu khác nhau, mọi request sẽ bị từ chối với lỗi `401 Unauthorized`.

---

## 🔍 Debug & Logging

Gateway log rất chi tiết để dễ theo dõi luồng request:

```
📥 [RECEIVE] POST /api/identity/public/auth/login
--- [GATEWAY SECURITY TRANSFORM] ---
🔑 Authenticated User: user@example.com
🆔 Injecting Header x-user-id: 4eb01951-a88e-43ec-abbd-43dcb71cfbae
➡️ [MATCHED] Route: workspaceRoute -> Forwarding to: http://localhost:3002
✅ [COMPLETED] Status: 200
```

---

## 📁 Cấu Trúc File

```
YarpGateway/
├── Program.cs              # Entry point — Toàn bộ config nằm ở đây
├── appsettings.json        # Routing rules, JWT config, Rate Limit config
├── appsettings.Development.json
└── YarpGateway.csproj      # Khai báo dependencies
```
