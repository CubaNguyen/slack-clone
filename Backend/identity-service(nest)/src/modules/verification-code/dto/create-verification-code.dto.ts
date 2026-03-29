import { IsDate, IsNotEmpty, IsNumber, IsString } from 'class-validator';

export class CreateVerificationCodeDto {
  @IsNumber()
  @IsNotEmpty()
  user_id: string;

  @IsString()
  @IsNotEmpty()
  code: string;

  @IsString()
  @IsNotEmpty()
  type: 'email_verification' | 'password_reset';

  @IsDate()
  @IsNotEmpty()
  expires_at: Date;
}
