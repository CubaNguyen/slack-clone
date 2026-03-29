import { SetMetadata } from '@nestjs/common';
import {
  registerDecorator,
  ValidationOptions,
  ValidationArguments,
} from 'class-validator';
import * as moment from 'moment-timezone';

export const IS_PUBLIC_KEY = 'isPublic';
export const Public = () => SetMetadata(IS_PUBLIC_KEY, true);

export function IsTimezone(validationOptions?: ValidationOptions) {
  return function (object: Object, propertyName: string) {
    registerDecorator({
      name: 'isTimezone',
      target: object.constructor,
      propertyName: propertyName,
      options: validationOptions,
      validator: {
        validate(value: any, args: ValidationArguments) {
          return (
            typeof value === 'string' && moment.tz.zone(value) !== null // check có tồn tại trong danh sách timezone không
          );
        },
        defaultMessage(args: ValidationArguments) {
          return 'Invalid timezone. Example: Asia/Ho_Chi_Minh, UTC, America/New_York';
        },
      },
    });
  };
}
