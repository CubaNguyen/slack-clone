import { Injectable } from '@nestjs/common';
import {
  VerificationCode,
  VerificationType,
} from './entities/verification-code.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { IsNull } from 'typeorm'; // Import IsNull nếu cần dùng cho find, nhưng update thì null là được
@Injectable()
export class VerificationCodeService {
  constructor(
    @InjectRepository(VerificationCode)
    private readonly verificationCodeRepository: Repository<VerificationCode>,
  ) {}

  async createCodeMail(
    user_id: string,
    code: string,
    // SỬA: Dùng trực tiếp Enum VerificationType để đảm bảo type-safe
    type: VerificationType,
    expires_at: Date,
  ) {
    // Vì user_id là UUID và trong Entity bạn đã khai báo @Column({ type: 'uuid' }) user_id
    // nên bạn có thể lưu trực tiếp user_id mà không cần load toàn bộ object User.

    return this.verificationCodeRepository.save({
      user_id, // Map thẳng vào cột user_id
      code,
      type, // Giá trị này phải là EMAIL_VERIFY hoặc RESET_PASSWORD...
      expires_at,
      used_at: null, // Mặc định là null (chưa dùng)
    });
  }
  async findByUserIdAndCode(
    user_id: string,
    code: string,
    type: VerificationType,
  ) {
    return this.verificationCodeRepository.findOne({
      where: { user_id: user_id, code, type: type },
    });
  }
  async markCodeAsUsed(id: string) {
    await this.verificationCodeRepository.update(
      { id }, // Điều kiện tìm
      { used_at: new Date() }, // Cập nhật trường used_at với thời gian hiện tại
    );
  }

  async invalidateOldCodes(user_id: string, type: VerificationType) {
    // Logic: Tìm các mã của user này, loại này, và chưa dùng (used_at is null)
    // Sau đó update used_at = now (coi như đã huỷ/đã dùng để không dùng lại được)

    await this.verificationCodeRepository.update(
      {
        user_id,
        type,
        used_at: IsNull(),
      },
      {
        used_at: new Date(),
      },
    );
  }

  // async create(dto: {
  //   user_id: string;
  //   code: string;
  //   type: string;
  //   expires_at: Date;
  // }) {
  //   const record = this.verificationCodeRepository.create({
  //     user_id: dto.user_id,
  //     code: dto.code,
  //     type: dto.type,
  //     expires_at: dto.expires_at,
  //     used_at: null,
  //   } as Partial<VerificationCode>);

  //   return await this.verificationCodeRepository.save(record);
  // }
}
