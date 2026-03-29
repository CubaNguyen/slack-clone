import { PartialType } from '@nestjs/mapped-types';
import { CreateProfileDto } from './create-profile.dto';
import { IsNotEmpty, IsOptional, IsString, IsUrl } from 'class-validator';
import { IsTimezone } from 'src/decorator/customize';

export class UpdateProfileDto extends PartialType(CreateProfileDto) {
  @IsString()
  @IsNotEmpty({ message: 'Full name is required' })
  full_name: string;

  @IsOptional()
  @IsUrl({}, { message: 'Avatar must be a valid URL' })
  avatar_url: string;

  @IsOptional()
  @IsString()
  bio: string;

  @IsTimezone({ message: 'Timezone không hợp lệ. Ví dụ: Asia/Ho_Chi_Minh' })
  timezone: string;
}
