import { IsEmail, MinLength } from 'class-validator';

export class CreateAuthDto {
  @IsEmail({}, { message: 'Email không hợp lệ' })
  email: string;
  @MinLength(6, { message: 'Password phải ít nhất 6 ký tự' })
  password: string;
}
