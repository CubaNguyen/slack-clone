import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  CreateDateColumn,
  Index,
} from 'typeorm';
// Định nghĩa cấu trúc Meta cố định
export interface OutboxMeta {
  event_id: string; // UUID của chính dòng outbox này
  trace_id: string; // Tracking ID từ request
  actor: {
    user_id: string;
    role?: string;
  };
  occurred_at: string; // ISO String
}

// Định nghĩa cấu trúc Payload tổng quát
// T là kiểu dữ liệu của phần "data" (ví dụ: UserCreatedData)
export interface OutboxPayload<T = any> {
  meta: OutboxMeta;
  data: T;
}
@Entity('outbox_events')
// Quan trọng: Index này giúp Worker tìm các event CHƯA XỬ LÝ cực nhanh
// Thay vì scan cả bảng, nó chỉ tìm những dòng processed_at = null
@Index(['processed_at', 'created_at'])
export class OutboxEvent {
  // UUID (PK)
  @PrimaryGeneratedColumn('uuid')
  id: string;

  // VARCHAR(50) - Loại Aggregate (VD: 'USER', 'CHANNEL')
  @Column({ type: 'varchar', length: 50 })
  aggregate_type: string;

  // UUID - ID của đối tượng bị thay đổi (VD: UserID)
  @Column({ type: 'uuid' })
  aggregate_id: string;

  // VARCHAR(50) - Tên sự kiện (VD: 'USER_CREATED', 'PASSWORD_CHANGED')
  @Column({ type: 'varchar', length: 50 })
  event_type: string;

  // JSONB - Lưu trữ payload có cấu trúc
  // TypeORM sẽ tự động stringify/parse JSON khi lưu/đọc
  @Column({ type: 'jsonb' })
  payload: OutboxPayload;

  // TIMESTAMP - Thời điểm tạo event
  @CreateDateColumn({ type: 'timestamp' })
  created_at: Date;

  // TIMESTAMP NULL - Thời điểm đã đẩy vào RabbitMQ/Kafka thành công
  // Null = Chưa xử lý (Pending)
  @Column({ type: 'timestamp', nullable: true })
  processed_at: Date | null;
}
