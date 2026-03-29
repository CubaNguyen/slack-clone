import {
  Column,
  CreateDateColumn,
  DeleteDateColumn,
  Entity,
  OneToMany,
  OneToOne,
  PrimaryGeneratedColumn,
  UpdateDateColumn,
} from 'typeorm';
import { Profile } from '../../profile/entities/profile.entity';
import { Session } from '../../session/entities/session.entity';
import { VerificationCode } from '../../verification-code/entities/verification-code.entity';

// 1. Định nghĩa Enum cho Status
export enum UserStatus {
  ACTIVE = 'ACTIVE',
  DISABLED = 'DISABLED',
}

@Entity('users')
export class User {
  // UUID (PK)
  @PrimaryGeneratedColumn('uuid')
  id: string;

  // VARCHAR(255) NOT NULL
  @Column({ type: 'varchar', length: 255, unique: true })
  email: string;

  // VARCHAR(255) NOT NULL - Đổi tên từ password sang password_hash
  @Column({ type: 'varchar', length: 255, name: 'password_hash' })
  password_hash: string;

  // BOOLEAN DEFAULT FALSE
  @Column({ default: false })
  is_email_verified: boolean;

  // ENUM('ACTIVE', 'DISABLED')
  @Column({
    type: 'enum',
    enum: UserStatus,
    default: UserStatus.ACTIVE,
  })
  status: UserStatus;

  // BOOLEAN DEFAULT FALSE - Đổi tên từ profile_complete
  @Column({ default: false, name: 'profile_completed' })
  profile_completed: boolean;

  // TIMESTAMP
  @CreateDateColumn({ type: 'timestamp' })
  created_at: Date;

  // TIMESTAMP
  @UpdateDateColumn({ type: 'timestamp' })
  updated_at: Date;

  // TIMESTAMP NULL - Đây là tính năng Soft Delete
  @DeleteDateColumn({ type: 'timestamp', nullable: true })
  deleted_at: Date;

  // --- RELATIONS ---
  // Lưu ý: Các bảng quan hệ (Profile, Session...) cũng phải sửa user_id sang kiểu UUID

  @OneToOne(() => Profile, (profile) => profile.user)
  // (Trống không, không có @JoinColumn ở đây)
  profile: Profile;

  @OneToMany(() => Session, (session) => session.user)
  sessions: Session[];

  @OneToMany(() => VerificationCode, (code) => code.user)
  verification_codes: VerificationCode[];
}
