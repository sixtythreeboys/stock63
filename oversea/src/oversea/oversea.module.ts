import { MiddlewareConsumer, Module, NestModule } from '@nestjs/common';
import { OverseaController } from './oversea.controller';
import { OverseaService } from './oversea.service';
import { HttpModule } from '@nestjs/axios';
import { checkTokenMiddleware } from './oversea.middleware';

@Module({
  imports: [HttpModule],
  controllers: [OverseaController],
  providers: [OverseaService],
})
export class OverseaModule implements NestModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(checkTokenMiddleware).forRoutes('oversea');
  }
}
