import { Body, Controller, Get, Param, Post, Res } from '@nestjs/common';
import { OverseaService } from './oversea.service';
import { Response } from 'express';

@Controller('oversea')
export class OverseaController {
  constructor(private readonly oversea: OverseaService) {}
  @Get('HHDFS76410000')
  async HHDFS76410000(@Res() res: Response, @Param() params: any) {
    this.oversea
      .HHDFS76410000(params)
      .then((e) => {
        const { status, data } = e;
        res.status(status).send(data);
      })
      .catch((e) => {
        const { status, data } = e.response;
        res.status(status).send(data);
      });
  }
}
