import { PartialType } from '@nestjs/mapped-types';
import { CreateProfileDto } from './create-profile.dto';
import {
  IsNotEmpty,
  IsOptional,
  IsString,
  IsUrl,
  MaxLength,
  MinLength,
} from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class UpdateProfileDto extends PartialType(CreateProfileDto) {
  @ApiProperty({ example: 'Nguyen Van A' })
  @IsString()
  @IsOptional()
  @IsNotEmpty({ message: 'Họ và tên không được để trống' }) // 👈 Chặn chuỗi rỗng ""
  @MinLength(2, { message: 'Họ và tên quá ngắn' })
  @MaxLength(50)
  full_name?: string;

  @ApiProperty({ required: false })
  @IsString()
  @IsOptional()
  avatar_url?: string;

  @ApiProperty({ required: false })
  @IsString()
  @IsOptional()
  @MaxLength(200)
  bio?: string;
}
