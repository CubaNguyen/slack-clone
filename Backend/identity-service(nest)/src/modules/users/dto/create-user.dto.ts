/* eslint-disable @typescript-eslint/no-unsafe-call */
import { IsEmail, IsNotEmpty, MinLength, ValidateIf } from 'class-validator';
export class CreateUserDto {
  @IsEmail({}, { message: 'Email không hợp lệ' })
  email: string;
  @MinLength(6, { message: 'Password phải ít nhất 6 ký tự' })
  password: string;
}
