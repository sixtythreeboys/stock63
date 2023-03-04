import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { apply as eureka } from './common/eureka';
import config from 'config';
import init from './common/init';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  await init();
  await app.listen(config.APP.PORT);
  eureka();
}
bootstrap();
