import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { User, UserStatus } from './entities/user.entity';

@Injectable()
export class UsersService {
  constructor(
    @InjectRepository(User)
    private readonly userRepo: Repository<User>,
  ) {}

  // async create(dto: CreateUserDto): Promise<User> {
  //   // Hash password
  //   const hashedPassword = await hashPassword(dto.password);

  //   const newUser = this.userRepo.create({
  //     email: dto.email,
  //     hashedPassword: hashedPassword,
  //   });

  //   return this.userRepo.save(newUser);
  // }
  async signUpService(email: string, hashedPassword: string) {
    const newUser = await this.userRepo.create({
      email,
      password_hash: hashedPassword,
    });
    return this.userRepo.save(newUser);
  }

  async verifyEmailUser(id: string) {
    return this.userRepo.update(
      { id: id },
      {
        is_email_verified: true,
        status: UserStatus.ACTIVE, // <--- BỔ SUNG: Kích hoạt tài khoản luôn
      },
    );
  }

  async updateProfileCompletion(userId: string, profile_complete: boolean) {
    await this.userRepo.update(
      { id: userId },
      { profile_completed: profile_complete },
    );
    return await this.userRepo.findOne({ where: { id: userId } });
  }

  async getMe(userId: string) {
    const user = await this.userRepo.findOne({
      where: { id: userId },
      relations: ['profile'], // 👈 Tự động JOIN sang bảng profiles
    });

    if (!user) {
      throw new NotFoundException('User not found');
    }

    // Bảo mật: Xóa hash mật khẩu trước khi gửi về Client
    (user as any).password_hash = undefined;
    return {
      success: true,
      data: user,
    };
  }
  async findByEmail(email: string) {
    const user = await this.userRepo.findOne({
      where: { email: email },
      relations: ['profile'], // 👈 Quan trọng: Join để lấy Avatar/FullName nếu cần hiển thị
    });

    // 1. Nếu không tìm thấy
    // Với gRPC/Internal Service: Thường trả về null để Controller tự xử lý (trả về empty object)
    // Thay vì ném lỗi 404 ngay tại đây.
    if (!user) {
      return null;
    }

    // 2. Bảo mật: Xóa hash mật khẩu (giống getMe)
    // Dùng delete hoặc gán undefined để không lộ ra ngoài
    if ((user as any).password_hash) {
      (user as any).password_hash = undefined;
    }

    // 3. Trả về User Entity nguyên bản
    // Lưu ý: Không wrap trong { success: true, data: ... } vì gRPC Controller cần object User gốc
    // để map sang Proto Message.
    return user;
  }
}
