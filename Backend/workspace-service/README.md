<div align="center">

# 🏢 Workspace Service

**Trái tim nghiệp vụ của nền tảng — Quản lý toàn bộ Workspace, Channel và thành viên theo kiến trúc DDD.**

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-Producer-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![gRPC](https://img.shields.io/badge/gRPC-Client-00A98F?style=for-the-badge)
![Redis](https://img.shields.io/badge/Redis-Cache-DC382D?style=for-the-badge&logo=redis&logoColor=white)

</div>

---

## 📋 Tổng Quan

**Workspace Service** là service phức tạp nhất về mặt nghiệp vụ trong hệ thống. Nó quản lý toàn bộ "cấu tổ chức" của một Slack workspace: từ việc tạo workspace, mời thành viên, tạo và quản lý các kênh (channels), cho đến xây dựng toàn bộ bộ dữ liệu hiển thị thanh sidebar.

Service được kiến trúc theo hai nguyên tắc thiết kế phần mềm hàng đầu:
- **Domain-Driven Design (DDD)**: Nghiệp vụ được mô hình hóa bằng Aggregates, Value Objects, Domain Events, và Repositories
- **CQRS (Command Query Responsibility Segregation)**: Tách biệt hoàn toàn luồng ghi (Commands) và luồng đọc (Queries)

---

## ✨ Tính Năng Cốt Lõi

### 1. 🏗️ Quản Lý Workspace

| API | Mô tả |
|---|---|
| `POST /workspaces` | Tạo workspace mới, người tạo tự động trở thành **Owner** |
| `GET /workspaces/mine` | Lấy tất cả workspace mà user đang là thành viên hoặc sở hữu |
| `GET /workspaces/{id}/sidebar/channels` | Lấy danh sách channels cho **Sidebar** (Public + Private mà user tham gia) |
| `GET /workspaces/{id}/browse/channels` | Lấy tất cả Public channels để người dùng **Browse & tham gia** |

### 2. 📣 Quản Lý Channel

| API | Mô tả |
|---|---|
| `POST /channels/workspaces/{workspaceId}` | Tạo channel mới (Public hoặc Private) |
| `POST /channels/workspace/{wsId}/{channelId}/join` | Tham gia một Public channel |
| `DELETE /channels/workspace/{wsId}/{channelId}/leave` | Rời khỏi một channel |

### 3. 📨 Hệ Thống Mời Thành Viên (Invitation Flow)

Đây là tính năng phức tạp và tinh tế nhất của service. Toàn bộ luồng mời thành viên được xử lý qua các bước sau:

```
Admin/Owner gửi lời mời qua email
          │
          ▼
[POST /invitations/workspace/{id}]
  ├── Gọi gRPC sang Identity Service: "Email này có tài khoản chưa?"
  ├── Sinh Invitation Token (JWT chứa workspaceId, email, role, TTL)
  ├── Lưu Invitation record vào DB
  └── Gửi email kèm link có token

          │
          ▼
Người được mời click link, FE gọi:
[GET /invitations/validate?token=...]
  ├── Giải mã và kiểm tra token
  ├── Trả về: { isValid, email, workspaceName, role, isUserExist }
  └── FE quyết định: Hiện form Login hay form Register

          │
          ▼
Người dùng đăng nhập/đăng ký xong → FE gọi:
[POST /invitations/accept]
  ├── Xác thực token
  ├── Verify email trong token khớp với user đang đăng nhập
  ├── Thêm user vào workspace với role tương ứng
  └── Trả về workspaceId để FE redirect vào

          │
          ▼
[Kafka] Publish event "MEMBER_JOINED" → Chat Service cập nhật quyền đọc tin nhắn
```

### 4. ⚡ Tích Hợp Đa Giao Thức

Service giao tiếp với phần còn lại của hệ thống qua **3 giao thức khác nhau**:

#### gRPC Client (Đồng bộ, cực nhanh)
Khi cần kiểm tra thông tin user trong quá trình mời thành viên, service gọi **gRPC sang Identity Service** thay vì REST. Lý do:
- Binary Protocol (Protobuf) nhanh hơn JSON ~7-10 lần
- Strongly typed contract, không sợ breaking changes im lặng
- Thích hợp cho internal service-to-service calls

```java
// Cấu hình trong application.properties
grpc.client.user-service.address=static://localhost:9090
grpc.client.user-service.negotiation-type=plaintext
```

#### Kafka Producer (Bất đồng bộ)
Khi một sự kiện domain quan trọng xảy ra (Workspace created, Member joined, Channel created...), service bắn một **Kafka event** lên topic `workspace-events`. Chat Service lắng nghe và tự đồng bộ dữ liệu:

```
Workspace Service         Kafka Topic           Chat Service
     │                  workspace-events             │
     │─── MEMBER_JOINED ──────────────────────────►  │
     │                                               │  Cập nhật: User này
     │                                               │  được đọc Channel X
```

#### Redis Cache
Dữ liệu đọc thường xuyên (Sidebar channels, Workspace list) được cache trong Redis để giảm tải database và tăng tốc độ response.

---

## 🏛️ Kiến Trúc DDD & CQRS

Service được tổ chức thành các tầng rõ ràng theo DDD:

```
src/main/java/com/project/workspace_service/
│
├── domain/                    # 🧠 Tầng Domain — Trái tim của nghiệp vụ
│   ├── aggregate/             #    Aggregates (Workspace, Channel, Invitation)
│   │   ├── workspace/         #    Workspace Aggregate (Root Entity)
│   │   ├── channel/           #    Channel Aggregate
│   │   └── invitation/        #    Invitation Aggregate
│   ├── valueobject/           #    Value Objects (WorkspaceName, SlugVO, Role...)
│   ├── event/                 #    Domain Events (WorkspaceCreated, MemberJoined...)
│   ├── repository/            #    Repository Interfaces (Contracts, not impl)
│   ├── service/               #    Domain Services (Logic span nhiều Aggregates)
│   └── enums/                 #    Enums (ChannelType, MemberRole, InvitationStatus)
│
├── application/               # 🎯 Tầng Application — Điều phối nghiệp vụ (CQRS)
│   ├── command/               #    Commands: Các hành động thay đổi trạng thái
│   │   ├── createworkspace/   #    CreateWorkspaceCommand + Handler
│   │   ├── createchannel/     #    CreateChannelCommand + Handler
│   │   ├── joinchannel/       #    JoinChannelCommand + Handler
│   │   ├── leavechannel/      #    LeaveChannelCommand + Handler
│   │   ├── invitemember/      #    InviteMemberCommand + Handler
│   │   └── acceptinvitation/  #    AcceptInvitationCommand + Handler
│   ├── query/                 #    Queries: Truy vấn dữ liệu (Read-only)
│   │   ├── workspace/list/    #    ListWorkspacesQuery + Handler
│   │   ├── channel/getSidebar/#    GetMySidebarChannelsQuery + Handler
│   │   ├── channel/listPublic/#    ListPublicChannelsQuery + Handler
│   │   └── invitation/        #    ValidateInvitationQuery + Handler
│   └── dto/                   #    Application-level DTOs
│
├── infrastructure/            # 🔧 Tầng Infrastructure — Kết nối thực tế
│   ├── persistence/           #    JPA Repositories (Impl cho domain contracts)
│   ├── grpc/                  #    gRPC Client stub (gọi sang Identity Service)
│   ├── kafka/                 #    Kafka Producer configuration
│   ├── redis/                 #    Redis Cache configuration
│   └── email/                 #    SMTP Mail Service (gửi email mời)
│
├── presentation/              # 🌐 Tầng Presentation — API Layer
│   └── rest/
│       ├── WorkspaceController.java
│       ├── ChannelController.java
│       ├── InvitationController.java
│       ├── WorkspaceMemberController.java
│       └── dto/               #    Request/Response DTOs
│
└── shared/                    # 🛠️ Tiện ích dùng chung
    ├── response/ApiResponse.java  # Wrapper response JSON chuẩn
    └── utils/SecurityUtils.java   # Đọc user info từ Header Gateway inject
```

---

## 🗄️ Schema Database Chính

### Bảng `workspaces`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | UUID PK | |
| `name` | VARCHAR | Tên workspace |
| `slug` | VARCHAR UNIQUE | Slug định danh URL |
| `owner_id` | UUID | ID của người sở hữu |
| `created_at` | TIMESTAMP | |

### Bảng `workspace_members`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | UUID PK | |
| `workspace_id` | UUID FK | |
| `user_id` | UUID | ID của thành viên (từ Identity Service) |
| `role` | ENUM | `OWNER` / `ADMIN` / `MEMBER` |
| `joined_at` | TIMESTAMP | |

### Bảng `channels`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | UUID PK | |
| `workspace_id` | UUID FK | |
| `name` | VARCHAR | Tên kênh |
| `type` | ENUM | `PUBLIC` / `PRIVATE` |
| `created_by` | UUID | Người tạo kênh |

### Bảng `invitations`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | UUID PK | |
| `workspace_id` | UUID FK | |
| `inviter_id` | UUID | Người gửi lời mời |
| `invited_email` | VARCHAR | Email được mời |
| `token` | TEXT UNIQUE | JWT Invitation Token |
| `role` | ENUM | Role sẽ được gán khi chấp nhận |
| `status` | ENUM | `PENDING` / `ACCEPTED` / `EXPIRED` |
| `expires_at` | TIMESTAMP | |

---

## ⚙️ Cài Đặt & Chạy

### Điều kiện
- Java 21 (JDK)
- Maven (hoặc dùng `mvnw` wrapper đính kèm)
- PostgreSQL >= 14 (Database: `slack_workspace`)
- Redis, Kafka đang chạy (từ `docker-compose up -d`)

```bash
cd workspace-service

# Chạy bằng Maven Wrapper (Không cần cài Maven global)
./mvnw spring-boot:run

# Hoặc build JAR và chạy
./mvnw clean package -DskipTests
java -jar target/workspace-service-*.jar
```

Service khởi động tại: `http://localhost:3002`

### Cấu Hình Quan Trọng (`application.properties`)
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/slack_workspace

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# gRPC Client (địa chỉ Identity Service)
grpc.client.user-service.address=static://localhost:9090

# Redis
spring.data.redis.host=localhost
```

---

## 📖 Swagger UI

```
http://localhost:3002/api/v1/swagger-ui.html
```

Tất cả các API đều có Swagger documentation với mô tả chi tiết request/response.

---

## 🔍 Điểm Nổi Bật Kỹ Thuật

- **Value Objects**: `WorkspaceName`, `SlugVO` đảm bảo tính hợp lệ của dữ liệu ngay tại lớp domain, không phụ thuộc vào validation ở tầng trên
- **Domain Events**: Mọi thay đổi trạng thái đều tạo ra Domain Event → được collect và publish lên Kafka sau khi transaction thành công
- **Aggregate Invariants**: Mọi business rule được enforce tại Aggregate Root, không thể bypass qua bất kỳ đường nào (kể cả gọi thẳng Repository)
- **Port & Adapter**: Repository interface nằm ở domain, implementation nằm ở infrastructure → dễ dàng swap ORM hoặc database
