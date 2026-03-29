import { ApiProperty } from '@nestjs/swagger';
import { Type } from 'class-transformer';
import {
  IsNotEmpty,
  IsNumber,
  IsString,
  IsUUID,
  Length,
} from 'class-validator';

export class VerifyEmailDto {
  @ApiProperty({
    example: 'd290f1ee-6c54-4b01-90e6-d701748f0851', // Thêm ví dụ cho Swagger đẹp hơn
    description: 'UUID của người dùng cần xác thực',
  })
  @IsUUID('4', { message: 'user_id phải là định dạng UUID' }) // Kiểm tra đúng chuẩn UUID
  @IsNotEmpty()
  user_id: string;

  @ApiProperty({
    example: '12345678',
    description: 'Mã xác thực nhận được qua email',
  })
  @IsString()
  @Length(8, 8, { message: 'Mã xác thực phải có đúng 8 ký tự' })
  code: string;
}
