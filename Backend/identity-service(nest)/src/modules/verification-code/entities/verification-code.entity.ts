import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  ManyToOne,
  CreateDateColumn,
  JoinColumn,
  Index,
} from 'typeorm';
import { User } from '../../users/entities/user.entity';

export enum VerificationType {
  EMAIL_VERIFY = 'EMAIL_VERIFY',
  RESET_PASSWORD = 'RESET_PASSWORD',
  INVITE_ACCEPT = 'INVITE_ACCEPT',
}

@Entity('verification_codes')
// Tạo Index để tìm kiếm nhanh khi user submit code
@Index(['user_id', 'code', 'type'])
export class VerificationCode {
  // UUID (PK)
  @PrimaryGeneratedColumn('uuid')
  id: string;

  // UUID (FK users.id)
  @Column({ type: 'uuid' })
  user_id: string;

  // Relation: Map với bảng Users
  @ManyToOne(() => User, (user) => user.verification_codes, {
    onDelete: 'CASCADE',
  })
  @JoinColumn({ name: 'user_id' })
  user: User;

  @Column({ type: 'varchar', length: 10 })
  code: string;

  // ENUM type
  @Column({
    type: 'enum',
    enum: VerificationType,
  })
  type: VerificationType;

  // TIMESTAMP - Thời điểm hết hạn
  @Column({ type: 'timestamp' })
  expires_at: Date;

  // TIMESTAMP NULL - Thay thế cho boolean used.
  // Nếu null = chưa dùng. Nếu có ngày giờ = đã dùng.
  @Column({ type: 'timestamp', nullable: true })
  used_at: Date | null;

  // TIMESTAMP - Ngày tạo
  @CreateDateColumn({ type: 'timestamp' })
  created_at: Date;
}
