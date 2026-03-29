import { Module } from '@nestjs/common';
import { AuthService } from './auth.service';
import { AuthController } from './auth.controller';
import { UsersModule } from '../modules/users/users.module';
import { JwtModule } from '@nestjs/jwt';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from '../modules/users/entities/user.entity';
import { PassportModule } from '@nestjs/passport';
import { LocalStrategy } from './passport/local.strategy';
import { JwtStrategy } from './passport/jwt.strategy';
import { VerificationCodeModule } from 'src/modules/verification-code/verification-code.module';
import { MailModule } from 'src/modules/mailer/mail.module';
import { Session } from 'src/modules/session/entities/session.entity';
import { SessionModule } from 'src/modules/session/session.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([User, Session]),

    PassportModule.register({ defaultStrategy: 'jwt' }),
    UsersModule,
    ConfigModule,
    VerificationCodeModule,
    SessionModule,
    MailModule,
    PassportModule.register({ defaultStrategy: 'jwt' }),
    JwtModule.registerAsync({
      imports: [ConfigModule],
      inject: [ConfigService],
      useFactory: (config: ConfigService) => ({
        global: true,
        secret: config.get<string>('JWT_SECRET'),
        signOptions: { expiresIn: config.get<string>('JWT_EXPIRATION') },
      }),
    }),
  ],
  controllers: [AuthController],
  providers: [AuthService, LocalStrategy, JwtStrategy],
  exports: [
    AuthService,
    JwtModule, // ✅ thêm dòng này để chia sẻ JwtService cho module khác
  ],
})
export class AuthModule {}
