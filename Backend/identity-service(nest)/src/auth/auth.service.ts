import {
  BadRequestException,
  ConflictException,
  Injectable,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { InjectRepository } from '@nestjs/typeorm';
import dayjs from 'dayjs';
import { MailService } from 'src/modules/mailer/mail.service';
import { UsersService } from 'src/modules/users/users.service';
import { VerificationType } from 'src/modules/verification-code/entities/verification-code.entity';
import { comparePassword, hashPassword } from 'src/utils/bcrypt.util';
import { generateAlphaNumericCode } from 'src/utils/generateCode.util';
import { Repository } from 'typeorm';
import { Session } from '../modules/session/entities/session.entity';
import { User } from '../modules/users/entities/user.entity';
import { VerificationCodeService } from './../modules/verification-code/verification-code.service';
import { ChangePasswordDto } from './dto/change-password.dto';
import { CreateAuthDto } from './dto/create-auth.dto';
import { ResetPasswordDto } from './dto/reset-password.dto';
@Injectable()
export class AuthService {
  constructor(
    @InjectRepository(User)
    private usersRepository: Repository<User>,
    private jwtService: JwtService,
    private usersService: UsersService,
    private verificationCodeService: VerificationCodeService,
    private mailService: MailService,
    @InjectRepository(Session)
    private sessionRepository: Repository<Session>,
  ) {}

  async validateUser(email: string, password_hash: string) {
    // check email, password_hash
    const user = await this.usersRepository.findOne({ where: { email } });

    if (!user) return null;
    const passwordValid = await comparePassword(
      password_hash,
      user.password_hash,
    );
    if (!passwordValid) {
      return null;
    }
    // remove password_hash from user object
    const { password_hash: _, ...result } = user;

    return result;
  }

  async signIn(user: any) {
    if (!user.is_email_verified) {
      throw new UnauthorizedException(
        'Email chưa được xác minh. Vui lòng kiểm tra hộp thư.',
      );
    }
    const payload = {
      sub: user.id,
      email: user.email,
      status: user.status,
      profile_completed: user.profile_completed,
      type: 'access_token',
    };
    // 3️⃣ Sinh Access token (ngắn hạn, ví dụ 15 phút)
    const access_token = await this.jwtService.signAsync(payload, {
      expiresIn: '15m',
    });

    // 4️⃣ Sinh Refresh token (dài hơn, ví dụ 7 ngày)
    const refresh_payload = {
      sub: user.id,
      type: 'refresh',
    };
    const refresh_token = await this.jwtService.signAsync(refresh_payload, {
      expiresIn: '7d',
    });
    const expires_at = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000);
    await this.sessionRepository.delete({ user_id: user.id });
    // 5️⃣ Lưu refresh token vào bảng sessions
    await this.sessionRepository.save({
      user_id: user.id,
      refresh_token,
      expires_at: expires_at,
    });
    return {
      message: 'Login successful',
      data: {
        access_token,
        refresh_token,
        user: {
          id: user.id,
          email: user.email,
          profile_completed: user.profile_completed,
        },
      },
    };
  }

  async signUp(dto: CreateAuthDto) {
    try {
      // 1. Check user đã tồn tại chưa
      const existed = await this.usersRepository.findOne({
        where: { email: dto.email },
      });
      if (existed) {
        throw new ConflictException({
          message: 'User already exists',
          code: 'USER_ALREADY_EXISTS',
        });
      }

      // 2. Hash password_hash
      const hashedPassword = await hashPassword(dto.password);

      console.log('Step 3: Creating User...');
      // 3. Save user to DB
      const user = await this.usersService.signUpService(
        dto.email,
        hashedPassword,
      );
      console.log('User created:', user?.id); // Nếu không thấy log này -> Lỗi ở signUpService
      // 4. Create a email code
      const code = generateAlphaNumericCode();
      const expires = dayjs().add(10, 'minute').toDate(); // hết hạn sau 10 phút
      // expires.setMinutes(expires.getMinutes() + 10);
      await this.verificationCodeService.createCodeMail(
        user.id,
        code,
        VerificationType.EMAIL_VERIFY,
        expires,
      );

      await this.mailService.sendMail(user.email, code);
      return {
        message: 'Please check your inbox to verify your email address',
        data: user.id,
      };
    } catch (e) {
      console.error('Error in signUp:', e);
      throw e;
    }
  }

  async verifyEmail(user_id: string, code: string) {
    // 1. SỬA: Dùng Enum thay vì string cứng
    const type = VerificationType.EMAIL_VERIFY;

    // Gọi service tìm code (nhớ truyền Enum vào)
    const record = await this.verificationCodeService.findByUserIdAndCode(
      user_id,
      code,
      type,
    );

    // 2. Các check validation (Giữ nguyên, logic tốt)
    if (!record) {
      throw new BadRequestException({
        message: 'Invalid mail code',
        code: 'INVALID_MAIL_CODE',
      });
    }
    if (record.used_at) {
      throw new BadRequestException({
        message: 'Code already used',
        code: 'CODE_ALREADY_USED',
      });
    }
    if (record.expires_at < new Date()) {
      throw new BadRequestException({
        message: 'Code expired',
        code: 'CODE_EXPIRED',
      });
    }

    // 3. Update User: Vừa verify email, vừa set status thành ACTIVE
    await this.usersService.verifyEmailUser(user_id);

    // 4. Đánh dấu code đã dùng (Đổi tên hàm cho đúng ý nghĩa)
    await this.verificationCodeService.markCodeAsUsed(record.id);

    return { message: 'Email verified successfully' };
  }

  async resendVerificationEmail(email: string) {
    const user = await this.usersRepository.findOne({ where: { email } });

    // 1. Kiểm tra User tồn tại
    if (!user) {
      throw new NotFoundException({
        message: 'Email không tồn tại trong hệ thống.',
        code: 'USER_NOT_FOUND',
      });
    }

    // 2. Kiểm tra xem đã verify chưa
    if (user.is_email_verified) {
      throw new BadRequestException({
        message: 'Email đã được xác minh trước đó.',
        code: 'EMAIL_ALREADY_VERIFIED',
      });
    }

    // 3. Vô hiệu hóa mã cũ (Dùng Enum)
    await this.verificationCodeService.invalidateOldCodes(
      user.id,
      VerificationType.EMAIL_VERIFY,
    );

    // 4. Tạo mã mới
    const code = generateAlphaNumericCode(); // Hàm này của bạn giữ nguyên

    // SỬA LỖI THỜI GIAN: Dùng Date thuần cho đơn giản và chính xác
    const expires = new Date();
    expires.setMinutes(expires.getMinutes() + 10); // Cộng 10 phút từ hiện tại

    // 5. Lưu vào DB (Dùng Enum)
    await this.verificationCodeService.createCodeMail(
      user.id,
      code,
      VerificationType.EMAIL_VERIFY,
      expires,
    );

    // 6. Gửi mail
    await this.mailService.sendMail(user.email, code);

    return {
      message: 'Mã xác thực mới đã được gửi. Vui lòng kiểm tra email.',
      data: { user_id: user.id }, // Trả về object rõ ràng hơn
    };
  }

  async forgotPassword(email: string) {
    const user = await this.usersRepository.findOne({ where: { email } });
    if (!user) throw new NotFoundException('User not found');
    await this.verificationCodeService.invalidateOldCodes(
      user.id,
      VerificationType.RESET_PASSWORD,
    );
    const code = generateAlphaNumericCode();

    const expires = dayjs().add(10, 'minute').toDate(); // hết hạn sau 10 phút

    expires.setMinutes(expires.getMinutes() + 10);
    const type = VerificationType.RESET_PASSWORD;
    await this.verificationCodeService.createCodeMail(
      user.id,
      code,
      type,
      expires,
    );

    await this.mailService.sendMailForgetPass(user.email, code);
    return { message: 'Verification code sent to email' };
  }
  async resetPassword(dto: ResetPasswordDto) {
    const user = await this.usersRepository.findOne({
      where: { email: dto.email },
    });
    if (!user) throw new NotFoundException('User not found');

    const record = await this.verificationCodeService.findByUserIdAndCode(
      user.id,
      dto.code,
      VerificationType.RESET_PASSWORD, // Dùng Enum
    );

    if (!record)
      throw new BadRequestException({
        message: 'Invalid mail code',
        code: 'INVALID_MAIL_CODE',
      });
    if (record.used_at)
      throw new BadRequestException({
        message: 'Code already used',
        code: 'CODE_ALREADY_USED',
      });
    if (record.expires_at < new Date())
      throw new BadRequestException({
        message: 'Code expired',
        code: 'CODE_EXPIRED',
      });

    // Cập nhật mật khẩu
    user.password_hash = await hashPassword(dto.newPassword);
    await this.usersRepository.save(user);

    // Đánh dấu code đã dùng
    await this.verificationCodeService.markCodeAsUsed(record.id);
    await this.sessionRepository.delete({ user_id: user.id });
    return { message: 'Password reset successful' };
  }

  // // auth.service.ts
  async changePassword(userId: string, dto: ChangePasswordDto) {
    const user = await this.usersRepository.findOne({ where: { id: userId } });
    if (!user) throw new NotFoundException('User not found');

    const isMatch = await comparePassword(dto.oldPassword, user.password_hash);
    if (!isMatch)
      throw new BadRequestException('Old password_hash is incorrect');

    if (dto.oldPassword === dto.newPassword) {
      throw new BadRequestException(
        'Mật khẩu mới không được trùng với mật khẩu cũ',
      );
    }

    user.password_hash = await hashPassword(dto.newPassword);
    await this.usersRepository.save(user);

    return { message: 'Password changed successfully' };
  }

  async refreshToken(refresh_token: string) {
    try {
      // ✅ 1. Decode refresh token để lấy userId
      const payload = this.jwtService.verify(refresh_token);
      const userId = payload.sub;

      // ✅ 2. Kiểm tra token có tồn tại trong DB không
      const session = await this.sessionRepository.findOne({
        where: { user_id: userId, refresh_token },
      });
      if (!session) {
        throw new UnauthorizedException(
          'Refresh token không tồn tại hoặc đã bị thu hồi',
        );
      }

      // ✅ 3. Kiểm tra token còn hạn không
      if (new Date() > session.expires_at) {
        throw new UnauthorizedException('Refresh token đã hết hạn');
      }

      // ✅ 4. Lấy lại thông tin user mới nhất (để cập nhật profile_complete, email, v.v.)
      const user = await this.usersRepository.findOne({
        where: { id: userId },
      });
      if (!user) throw new NotFoundException('User không tồn tại');
      if (user.status === 'DISABLED') {
        // Hoặc check Enum
        throw new UnauthorizedException('Tài khoản đã bị vô hiệu hóa');
      }
      const newPayload = {
        sub: user.id,
        email: user.email,
        status: user.status, // <--- Thêm cái này
        profile_completed: user.profile_completed,
        type: 'access_token', // <--- Thêm cái này
      };

      // ✅ 5. Sinh access token mới
      const new_access_token = await this.jwtService.signAsync(newPayload, {
        expiresIn: '15m',
      });

      return {
        message: 'Token refreshed successfully',
        data: {
          access_token: new_access_token,
          refresh_token, // giữ nguyên token cũ
        },
      };
    } catch (error) {
      console.error('⚠️ Error refreshing token:', error);
      throw new UnauthorizedException('Invalid or expired token');
    }
  }

  async logout(userId: string, refresh_token: string) {
    // Xóa đúng cái session tương ứng với refresh_token đó
    // (Để không ảnh hưởng nếu user đang đăng nhập trên thiết bị khác)
    const result = await this.sessionRepository.delete({
      user_id: userId,
      refresh_token: refresh_token,
    });
    console.log('🚀 ~ AuthService ~ logout ~ result:', result);

    if (result.affected === 0) {
      // Tuỳ bạn: có thể báo lỗi hoặc cứ báo thành công để hacker không biết
      // Mình khuyên cứ báo success để FE dễ xử lý
    }

    return { message: 'Đăng xuất thành công' };
  }
}
