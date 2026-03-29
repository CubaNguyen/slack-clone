import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as nodemailer from 'nodemailer';
import {
  forgotPasswordEmailTemplate,
  verificationEmailTemplate,
} from './templates/verification.template';

@Injectable()
export class MailService {
  private transporter;

  constructor(private configService: ConfigService) {
    this.transporter = nodemailer.createTransport({
      host: this.configService.get<string>('MAIL_HOST'),
      port: this.configService.get<number>('MAIL_PORT'),
      //   service: 'gmail', // dùng Gmail cho dễ test
      secure: false,
      auth: {
        user: this.configService.get<string>('SMTP_USER'),
        pass: this.configService.get<string>('SMTP_PASS'),
      },
    });
  }

  async sendMail(to: string, code: string) {
    const htmlTemplate = verificationEmailTemplate(code);
    return this.transporter.sendMail({
      from: `"No Reply" <${this.configService.get<string>('SMTP_USER')}>`,
      to,
      subject: 'Xác thực email',
      html: htmlTemplate,
    });
  }
  async sendMailForgetPass(to: string, code: string) {
    const htmlTemplate = forgotPasswordEmailTemplate(code);
    return this.transporter.sendMail({
      from: `"No Reply" <${this.configService.get<string>('SMTP_USER')}>`,
      to,
      subject: 'Khôi phục mật khẩu',
      html: htmlTemplate,
    });
  }
}
