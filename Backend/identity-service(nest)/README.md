<div align="center">

# 🔐 Identity Service

**Hệ thống quản lý danh tính và phiên đăng nhập cho toàn bộ nền tảng Slack Clone.**

![NestJS](https://img.shields.io/badge/NestJS-11-E0234E?style=for-the-badge&logo=nestjs&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5.7-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![gRPC](https://img.shields.io/badge/gRPC-Server-00A98F?style=for-the-badge)
![JWT](https://img.shields.io/badge/JWT-Auth-F7B731?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

</div>

---

## 📋 Tổng Quan

**Identity Service** là trái tim của hệ thống xác thực. Service này chịu trách nhiệm toàn bộ vòng đời của một tài khoản người dùng: từ lúc đăng ký, xác minh email, đăng nhập, quản lý phiên (sessions), đến khi đặt lại mật khẩu.

Được xây dựng trên **NestJS** với kiến trúc module rõ ràng, service vận hành song song hai giao thức:
- **REST API** (Port `3001`): Phục vụ tương tác từ phía người dùng
- **gRPC Server** (Port `9090`): Phục vụ giao tiếp nội bộ với các microservices khác

---

## ✨ Tính Năng Cốt Lõi

### 1. 🔑 Luồng Xác Thực Hoàn Chỉnh (Full Auth Flow)

```
Đăng ký (Register)
  ├── Kiểm tra email trùng lặp
  ├── Hash mật khẩu bằng bcrypt (salt rounds mạnh)
  ├── Tạo User record trong DB
  ├── Sinh mã OTP ngẫu nhiên (AlphaNumeric)
  ├── Lưu mã OTP vào DB với TTL 10 phút
  └── Gửi email xác minh qua SMTP Gmail

Xác minh Email (Verify Email)
  ├── Kiểm tra mã OTP hợp lệ
  ├── Kiểm tra mã chưa hết hạn
  ├── Kiểm tra mã chưa từng dùng
  └── Kích hoạt tài khoản (status: ACTIVE)

Đăng nhập (Sign In)
  ├── Validate email + password
  ├── Kiểm tra email đã được xác minh chưa
  ├── Sinh Access Token (JWT, hết hạn sau 15 phút)
  ├── Sinh Refresh Token (JWT, hết hạn sau 7 ngày)
  └── Lưu Refresh Token vào bảng sessions (DB)
```

### 2. 🔄 Token Refresh & Session Management

Hệ thống áp dụng mô hình **Dual Token** (Access + Refresh) để cân bằng giữa bảo mật và trải nghiệm người dùng:

| Token | Thời hạn | Lưu ở đâu | Dùng để làm gì |
|---|---|---|---|
| `access_token` | **15 phút** | Memory (Client) | Gọi mọi API được bảo vệ |
| `refresh_token` | **7 ngày** | Database `sessions` | Lấy `access_token` mới khi hết hạn |

Khi Refresh Token được dùng:
- Xác minh token tồn tại trong DB (không bị thu hồi/"logout")
- Kiểm tra còn hạn sử dụng
- Lấy thông tin user **mới nhất** từ DB (cập nhật profile_completed, status...)
- Sinh access_token mới với payload cập nhật

### 3. 📧 Email Verification System

Mọi địa chỉ email phải được xác minh trước khi có thể đăng nhập:

```
Người dùng nhận email → Nhập mã 6 ký tự → Server kiểm tra:
  ├── Mã có tồn tại trong DB không?
  ├── Mã đã dùng rồi chưa? (Chống replay attack)
  └── Mã đã hết 10 phút chưa? (TTL)
```

Tính năng **Gửi lại mã** (`resendVerificationEmail`):
- Vô hiệu hóa toàn bộ mã cũ cho user đó
- Sinh mã mới và gửi email lại

### 4. 🔒 Password Management

- **Quên mật khẩu**: Gửi mã OTP qua email → Nhập mã + mật khẩu mới → Reset thành công + Thu hồi toàn bộ sessions cũ
- **Đổi mật khẩu**: Xác thực mật khẩu cũ trước → Hash mật khẩu mới → Lưu DB
- **Validation**: Mật khẩu mới không được trùng mật khẩu cũ

### 5. 🚀 gRPC Server — Phục vụ Microservices Nội Bộ

Service expose một **gRPC server** cho phép các service khác (cụ thể là Workspace Service) truy vấn thông tin user với tốc độ cực thấp (sub-millisecond) mà không cần gọi qua HTTP:

```protobuf
// Định nghĩa proto contract
service UserService {
  rpc GetUserForInvite (GetUserForInviteRequest) returns (UserInviteInfo);
}

message GetUserForInviteRequest {
  string email = 1;
}

message UserInviteInfo {
  string id    = 1;
  string email = 2;
}
```

**Usecase**: Khi Workspace Service muốn mời member qua email, nó gọi gRPC sang Identity Service để kiểm tra "email này đã có tài khoản chưa?" — nhanh hơn REST nhiều lần vì dùng Binary Protocol (Protobuf).

### 6. 📤 Outbox Pattern (Đảm bảo tính nhất quán sự kiện)

Identity Service triển khai **Transactional Outbox Pattern** để đảm bảo sự kiện domain (User Created, Email Verified...) không bao giờ bị mất ngay cả khi hệ thống mất điện giữa chừng:

```
Transaction DB bắt đầu:
  ├── [1] Lưu User vào bảng users          ← Nghiệp vụ chính
  └── [2] Lưu event vào bảng outbox_events ← Ghi nhật ký sự kiện
Transaction commit (cả hai hoặc không cái nào)

Background Worker chạy riêng biệt:
  ├── Quét outbox_events WHERE processed_at IS NULL
  ├── Đẩy lên Message Broker (RabbitMQ/Kafka)
  └── Cập nhật processed_at = NOW()
```

Bảng `outbox_events` được đánh index trên `(processed_at, created_at)` để Worker tìm kiếm hiệu quả.

---

## 📁 Cấu Trúc Module

```
src/
├── auth/                           # 🔑 Core Authentication
│   ├── auth.service.ts             #    Logic đăng ký, đăng nhập, refresh token...
│   ├── auth.controller.ts          #    REST endpoints
│   ├── auth.module.ts
│   ├── dto/                        #    Request DTOs (Validation rules)
│   │   ├── create-auth.dto.ts      #    DTO Đăng ký
│   │   ├── reset-password.dto.ts
│   │   └── change-password.dto.ts
│   ├── entities/
│   └── passport/                   #    Passport.js strategies (Local, JWT)
│
├── modules/
│   ├── users/                      # 👤 Quản lý User entity
│   ├── session/                    # 🗂️ Quản lý Refresh Token Sessions
│   ├── verification-code/          # 📨 Mã OTP cho Email & Reset Password
│   ├── mailer/                     # 📧 Wrapper gửi Email qua SMTP Gmail
│   ├── profile/                    # 🖼️ Quản lý Profile người dùng
│   └── outbox-events/              # 📤 Outbox Pattern implementation
│
├── proto/
│   └── user.proto                  # 📡 gRPC Service Contract
│
├── auth/passport/                  # Passport Strategies
├── interceptors/                   # Global response interceptors
├── decorator/                      # Custom decorators (e.g., @CurrentUser)
└── utils/
    ├── bcrypt.util.ts              # Hash/compare password
    └── generateCode.util.ts        # Sinh mã OTP ngẫu nhiên
```

---

## 🗄️ Schema Database Chính

### Bảng `users`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | UUID PK | Định danh duy nhất |
| `email` | VARCHAR UNIQUE | Email đăng nhập |
| `password_hash` | VARCHAR | Mật khẩu đã được bcrypt hash |
| `is_email_verified` | BOOLEAN | Đã xác minh email chưa |
| `status` | ENUM | `ACTIVE` / `DISABLED` |
| `profile_completed` | BOOLEAN | Đã điền đủ thông tin chưa |

### Bảng `sessions`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | UUID PK | |
| `user_id` | UUID FK | Liên kết đến User |
| `refresh_token` | TEXT | Token dài hạn (7 ngày) |
| `expires_at` | TIMESTAMP | Thời điểm hết hạn |

### Bảng `verification_codes`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | UUID PK | |
| `user_id` | UUID FK | |
| `code` | VARCHAR | Mã OTP 6 ký tự |
| `type` | ENUM | `EMAIL_VERIFY` / `RESET_PASSWORD` |
| `expires_at` | TIMESTAMP | Hết hạn sau 10 phút |
| `used_at` | TIMESTAMP NULL | NULL = Chưa dùng |

### Bảng `outbox_events`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | UUID PK | |
| `aggregate_type` | VARCHAR(50) | Loại đối tượng (VD: `USER`) |
| `aggregate_id` | UUID | ID của đối tượng thay đổi |
| `event_type` | VARCHAR(50) | Tên sự kiện (VD: `USER_CREATED`) |
| `payload` | JSONB | Dữ liệu chi tiết của sự kiện |
| `created_at` | TIMESTAMP | |
| `processed_at` | TIMESTAMP NULL | NULL = Chưa xử lý (Pending) |

---

## 🔌 API Endpoints

### Public (Không cần Token)
| Method | URL | Mô tả |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Đăng ký tài khoản mới |
| `POST` | `/api/v1/auth/login` | Đăng nhập |
| `POST` | `/api/v1/auth/verify-email` | Xác minh email bằng OTP |
| `POST` | `/api/v1/auth/resend-verification` | Gửi lại mã OTP |
| `POST` | `/api/v1/auth/forgot-password` | Yêu cầu đặt lại mật khẩu |
| `POST` | `/api/v1/auth/reset-password` | Đặt lại mật khẩu bằng OTP |
| `POST` | `/api/v1/auth/refresh-token` | Lấy Access Token mới |

### Secure (Yêu cầu Access Token)
| Method | URL | Mô tả |
|---|---|---|
| `POST` | `/api/v1/auth/logout` | Đăng xuất (Thu hồi Refresh Token) |
| `POST` | `/api/v1/auth/change-password` | Đổi mật khẩu (Cần xác thực mật khẩu cũ) |
| `GET` | `/api/v1/profile` | Lấy thông tin Profile |
| `PUT` | `/api/v1/profile` | Cập nhật thông tin Profile |

---

## ⚙️ Cài Đặt & Chạy

### Điều kiện
- Node.js >= 20
- PostgreSQL >= 14
- Gmail App Password (cho SMTP gửi email)

```bash
cd identity-service(nest)

# Cài dependencies
npm install

# Chạy development
npm run start:dev   # Có hot-reload

# Chạy production
npm run build
npm run start:prod
```

### Biến Môi Trường (.env)
```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=slack_identity
DB_USER=postgres
DB_PASS=your_password

# JWT
JWT_SECRET=your-super-secret-key

# Gmail SMTP
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USER=your-email@gmail.com
MAIL_PASS=your-app-password   # Lấy từ Google Account > App Passwords

# gRPC Server Port
GRPC_PORT=9090
```

---

## 📖 Swagger UI

Khi service đang chạy, truy cập tài liệu API tương tác tại:
```
http://localhost:3001/swagger
```
