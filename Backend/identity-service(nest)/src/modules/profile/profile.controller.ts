import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
  UseGuards,
  Put,
  Req,
} from '@nestjs/common';
import { ProfileService } from './profile.service';
import { UpdateProfileDto } from './dto/update-profile.dto';
import { Public } from 'src/decorator/customize';
import { JwtAuthGuard } from 'src/auth/passport/jwt-auth.guard';
import { ApiBearerAuth } from '@nestjs/swagger';

@Controller('profile')
export class ProfileController {
  constructor(private readonly profileService: ProfileService) {}

  //   @Get(':id')
  //   async getProfile(@Param('id') userid: string) {
  //     const profile = await this.profileService.getProfileByUserId(userId);

  //     return profile;
  //   }
  //   @Public()
  //   @Get('internal/:id')
  //   async getProfileInternal(@Param('id') userid: string) {
  //     const profile = await this.profileService.getProfileByUserId(userId);

  //     return profile;
  //   }
  @UseGuards(JwtAuthGuard) // 1. Guard check token, giải mã lấy User ID gắn vào req.user
  @ApiBearerAuth()
  @Put()
  async updateProfile(@Req() req, @Body() updateProfileDto: UpdateProfileDto) {
    const user_id = req.user.id; // lấy từ JWT payload
    return this.profileService.updateOrCreate(user_id, updateProfileDto);
  }
}
