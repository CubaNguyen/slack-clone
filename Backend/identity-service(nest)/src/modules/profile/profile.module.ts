import { Module } from '@nestjs/common';
import { ProfileService } from './profile.service';
import { ProfileController } from './profile.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UsersModule } from '../users/users.module';
import { Profile } from './entities/profile.entity';
import { JwtModule } from '@nestjs/jwt';
import { Session } from '../session/entities/session.entity';
import { AuthModule } from 'src/auth/auth.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([Profile, Session]),
    UsersModule,
    AuthModule, // ✅ Thêm dòng này
  ],
  controllers: [ProfileController],
  providers: [ProfileService],
})
export class ProfileModule {}
