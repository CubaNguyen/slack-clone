import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsString } from 'class-validator';

export class LogoutDto {
  @ApiProperty({ description: 'Refresh token cần thu hồi' })
  @IsNotEmpty()
  @IsString()
  refresh_token: string;
}
