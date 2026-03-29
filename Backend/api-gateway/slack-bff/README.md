<div align="center">

# 📱 Slack BFF — Backend For Frontend

**Lớp trung gian thông minh, tối ưu hóa dữ liệu cho Mobile App.**

![NestJS](https://img.shields.io/badge/NestJS-11-E0234E?style=for-the-badge&logo=nestjs&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5.7-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Node.js](https://img.shields.io/badge/Node.js-22-339933?style=for-the-badge&logo=nodedotjs&logoColor=white)

</div>

---

## 📋 Tổng Quan

**Slack BFF** áp dụng mẫu kiến trúc **Backend For Frontend (BFF)** — một lớp API chuyên biệt được thiết kế riêng cho nhu cầu của Mobile client.

### Tại sao cần BFF?

Trong hệ thống microservices, mỗi service trả về dữ liệu theo domain của nó. Nhưng một màn hình trên mobile thường cần dữ liệu từ **nhiều services** cùng lúc. Nếu mobile phải tự gọi 3-4 API riêng lẻ, sẽ dẫn đến:

- ❌ Nhiều round-trip mạng → Tốn pin, tốn dữ liệu di động
- ❌ Code phức tạp ở Mobile để ghép dữ liệu
- ❌ Khó tối ưu performance riêng cho từng nền tảng

BFF giải quyết vấn đề này bằng cách **gộp nhiều service calls thành một API duy nhất**.

---

## ✨ Vai Trò & Chức Năng

```
Mobile App
    │
    │  1 request duy nhất
    ▼
┌─────────────────────────────┐
│        Slack BFF            │
│                             │
│  Gọi song song tới:         │
│  ├── Identity Service      │
│  ├── Workspace Service     │
│  └── Chat Service          │
│                             │
│  Gộp & format kết quả      │
│  theo nhu cầu Mobile        │
└─────────────────────────────┘
    │
    │  1 response tổng hợp
    ▼
Mobile App nhận đủ dữ liệu
```

### Tính năng cốt lõi

- **Request Aggregation**: Gộp nhiều API calls thành một request duy nhất
- **Data Transformation**: Format lại data theo khuôn mẫu tiêu thụ của Mobile UI
- **Parallel Calls**: Gọi các downstream services **đồng thời** (parallel), không đợi lần lượt, tối thiểu latency
- **Error Isolation**: Một service lỗi không làm sập toàn bộ response

---

## 🛠️ Công Nghệ Sử Dụng

| Thư viện | Phiên bản | Mục đích |
|---|---|---|
| `NestJS` | v11 | Framework chính (Dependency Injection, Modules) |
| `@nestjs/axios` | v4 | HTTP Client để gọi tới các Downstream Services |
| `axios` | v1.x | Underlying HTTP client (hỗ trợ interceptors, timeout) |
| `TypeScript` | v5.7 | Type safety, giảm lỗi runtime |

---

## ⚙️ Cài Đặt & Chạy

```bash
cd api-gateway/slack-bff

# Cài dependencies
npm install

# Chạy development (Hot reload)
npm run start:dev

# Build production
npm run build
npm run start:prod
```

Service sẽ chạy tại: `http://localhost:3300`

---

## 📁 Cấu Trúc File

```
slack-bff/
├── src/
│   ├── app.module.ts       # Root module
│   ├── app.controller.ts   # Health check endpoint
│   └── main.ts             # Bootstrap entry point
├── package.json
└── tsconfig.json
```

---

## 🔗 Kết Nối Trong Hệ Thống

```
[Mobile Client]
      │  /api/mobile-bff/*
      ▼
[YARP API Gateway]  — Xác thực JWT, inject x-user-id header
      │
      ▼
[Slack BFF :3300]   — Tổng hợp data, gọi các services nội bộ
```

> 📝 **Lưu ý cho Developer**: Mọi business logic liên quan đến "gộp data cho Mobile" nên được implement tại đây, **không** được thêm vào các domain services gốc (Identity, Workspace, Chat).
