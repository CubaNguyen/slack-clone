import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  OneToOne,
  JoinColumn,
  CreateDateColumn,
  UpdateDateColumn,
} from 'typeorm';
import { User } from '../../users/entities/user.entity';

@Entity('profiles')
export class Profile {
  // UUID (PK)
  @PrimaryGeneratedColumn('uuid')
  id: string;

  // UUID (FK users.id) - Khai báo cột này để có thể truy cập user_id trực tiếp mà không cần join
  @Column({ type: 'uuid', unique: true })
  user_id: string;

  // Relation: Map với bảng Users
  @OneToOne(() => User, (user) => user.profile, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'user_id' }) // Chỉ định cột user_id ở trên làm khóa ngoại
  user: User;

  // VARCHAR(150)
  @Column({ type: 'varchar', length: 150, nullable: true })
  full_name: string;

  // TEXT
  @Column({ type: 'text', nullable: true })
  avatar_url: string;

  // TEXT
  @Column({ type: 'text', nullable: true })
  bio: string;

  // TIMESTAMP
  @CreateDateColumn({ type: 'timestamp' })
  created_at: Date;

  // TIMESTAMP
  @UpdateDateColumn({ type: 'timestamp' })
  updated_at: Date;
}
