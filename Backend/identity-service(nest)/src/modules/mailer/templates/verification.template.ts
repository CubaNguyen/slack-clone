export function verificationEmailTemplate(code: string): string {
  return `
    <div style="font-family: Arial, sans-serif; padding: 20px;">
      <h2>Xác thực email của bạn</h2>
      <p>Cảm ơn bạn đã đăng ký. Đây là mã xác thực của bạn:</p>
      <div style="font-size: 24px; font-weight: bold; color: #4CAF50; margin: 10px 0;">
        ${code}
      </div>
      <p>Mã này sẽ hết hạn trong <b>10 phút</b>. Vui lòng không chia sẻ cho ai khác.</p>
      <hr>
      <p style="font-size: 12px; color: #777;">Nếu bạn không đăng ký, hãy bỏ qua email này.</p>
    </div>
  `;
}

export function forgotPasswordEmailTemplate(code: string): string {
  return `
    <div style="font-family: Arial, sans-serif; padding: 20px;">
      <h2>Khôi phục mật khẩu</h2>
      <p>Bạn vừa yêu cầu khôi phục mật khẩu cho tài khoản của mình. Đây là mã xác nhận:</p>
      <div style="font-size: 24px; font-weight: bold; color: #ff9800; margin: 10px 0;">
        ${code}
      </div>
      <p>Mã này sẽ hết hạn trong <b>15 phút</b>. Vui lòng không chia sẻ mã này cho bất kỳ ai.</p>
      <hr>
      <p style="font-size: 12px; color: #777;">Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>
    </div>
  `;
}
