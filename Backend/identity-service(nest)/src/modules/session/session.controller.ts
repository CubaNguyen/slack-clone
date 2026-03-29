import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
  HttpCode,
} from '@nestjs/common';
import { SessionService } from './session.service';
import { CreateSessionDto } from './dto/create-session.dto';
import { UpdateSessionDto } from './dto/update-session.dto';

@Controller('session')
export class SessionController {
  constructor(private readonly sessionService: SessionService) {}

  // // 🔹 [GET] /session/:token — kiểm tra session tồn tại
  // @Get(':token')
  // async getSessionByToken(@Param('token') token: string) {
  //   const session = await this.sessionService.findByToken(token);
  //   return session ? session : { message: 'Session not found' };
  // }

  // // 🔹 [DELETE] /session/user/:userId — xóa toàn bộ session của user
  // @Delete('user/:userId')
  // @HttpCode(204)
  // async deleteSessionsByUser(@Param('userId') userId: string) {
  //   await this.sessionService.deleteByUser(userId);
  // }

  // // 🔹 [DELETE] /session/token/:token — xóa 1 session theo token
  // @Delete('token/:token')
  // @HttpCode(204)
  // async deleteSessionByToken(@Param('token') token: string) {
  //   await this.sessionService.deleteByToken(token);
  // }
}
