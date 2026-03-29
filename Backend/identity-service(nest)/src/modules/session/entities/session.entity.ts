import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  ManyToOne,
  CreateDateColumn,
  JoinColumn,
} from 'typeorm';
import { User } from '../../users/entities/user.entity';

@Entity('sessions')
export class Session {
  // UUID (PK)
  @PrimaryGeneratedColumn('uuid')
  id: string;

  // UUID (FK users.id)
  @Column({ type: 'uuid' })
  user_id: string;

  // Relation: Map với bảng Users
  @ManyToOne(() => User, (user) => user.sessions, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'user_id' })
  user: User;

  // VARCHAR(255) UNIQUE - Cần Unique để query nhanh và tránh trùng lặp
  @Column({ type: 'varchar', length: 255, unique: true })
  refresh_token: string;

  // TEXT - Lưu User Agent (Trình duyệt, OS...)
  @Column({ type: 'text', nullable: true })
  user_agent: string;

  // VARCHAR(45) - Lưu IP (Đủ cho cả IPv4 và IPv6)
  @Column({ type: 'varchar', length: 45, nullable: true })
  ip_address: string;

  // TIMESTAMP - Thời gian hết hạn của token
  @Column({ type: 'timestamp' })
  expires_at: Date;

  // TIMESTAMP NULL - Nếu có giá trị nghĩa là token đã bị thu hồi (Logout)
  @Column({ type: 'timestamp', nullable: true })
  revoked_at: Date | null;

  // TIMESTAMP
  @CreateDateColumn({ type: 'timestamp' })
  created_at: Date;
}
