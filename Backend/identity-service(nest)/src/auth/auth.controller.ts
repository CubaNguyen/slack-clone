import {
  Body,
  Controller,
  Get,
  HttpCode,
  Post,
  Request,
  UseGuards,
} from '@nestjs/common';
import { AuthService } from './auth.service';

import { ApiBearerAuth, ApiBody } from '@nestjs/swagger';
import { Public } from 'src/decorator/customize';
import { VerifyEmailDto } from 'src/modules/verification-code/dto/VerifyEmailDto';
import { ResendVerificationDto } from 'src/modules/verification-code/dto/resend-verification.dto';
import { ChangePasswordDto } from './dto/change-password.dto';
import { CreateAuthDto } from './dto/create-auth.dto';
import { LoginDto } from './dto/login.dto';
import { LogoutDto } from './dto/logout.dto';
import { RefreshTokenDto } from './dto/refresh-token.dto';
import { ForgotPasswordDto, ResetPasswordDto } from './dto/reset-password.dto';
import { JwtAuthGuard } from './passport/jwt-auth.guard';
import { LocalAuthGuard } from './passport/local-auth.guard';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}
  @Public()
  @Get('test-gateway')
  test() {
    return 'NestJS is Alive!';
  }

  @UseGuards(LocalAuthGuard)
  @Post('signin')
  @Public()
  @ApiBody({ type: LoginDto })
  async login(@Request() req, @Body() loginDto: LoginDto) {
    return this.authService.signIn(req.user);
  }

  @Post('signup')
  @Public()
  async signUp(@Body() dto: CreateAuthDto) {
    // Implement your sign-up logic here

    return this.authService.signUp(dto);
  }
  // @Get('users/:id')
  // @Public()
  // async getUserById(@Param('id') id: string) {
  //   return this.authService.getUserById(id); // 👈 gọi qua service
  // }

  @Post('verify-email')
  @Public()
  async verifyEmail(@Body() dto: VerifyEmailDto) {
    return this.authService.verifyEmail(dto.user_id, dto.code);
  }

  @Post('resend-verification')
  @Public()
  async resendVerification(@Body() dto: ResendVerificationDto) {
    return this.authService.resendVerificationEmail(dto.email);
  }

  @Public()
  @Post('forgot-password')
  async forgotPassword(@Body() dto: ForgotPasswordDto) {
    return this.authService.forgotPassword(dto.email);
  }
  @Public()
  @Post('reset-password')
  async resetPassword(@Body() dto: ResetPasswordDto) {
    return this.authService.resetPassword(dto);
  }
  @UseGuards(JwtAuthGuard) // 1. Guard check token, giải mã lấy User ID gắn vào req.user
  @ApiBearerAuth()
  @Post('change-password')
  async changePassword(@Request() req, @Body() dto: ChangePasswordDto) {
    return this.authService.changePassword(req.user.id, dto);
  }
  @Public()
  @Post('refresh-token')
  async refreshToken(@Body() dto: RefreshTokenDto) {
    return this.authService.refreshToken(dto.refresh_token);
  }
  @UseGuards(JwtAuthGuard) // Phải có Access Token mới được gọi Logout
  @ApiBearerAuth()
  @Post('logout')
  @HttpCode(200) // Logout trả về 200 OK
  async logout(@Request() req, @Body() dto: LogoutDto) {
    return this.authService.logout(req.user.id, dto.refresh_token);
  }
}
