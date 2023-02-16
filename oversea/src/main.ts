import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { apply as eureka } from './common/eureka';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  await app.listen(3000);
  //eureka();
}
bootstrap();
