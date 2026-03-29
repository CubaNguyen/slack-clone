import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsString } from 'class-validator';

export class RefreshTokenDto {
  @ApiProperty({
    example: 'eyJhbGciOiJIUzI1NiIsIn...',
    description: 'Refresh token nhận được lúc login',
  })
  @IsNotEmpty()
  @IsString()
  refresh_token: string;
}
