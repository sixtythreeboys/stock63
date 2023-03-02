import axios from 'axios';
import config from 'config';
import { overseaModel } from './oversea.model';
import { NextFunction } from 'express';

export async function checkTokenMiddleware(
  req: Request,
  res: Response,
  next: NextFunction,
) {
  if (overseaModel.token.access_token_token_expired <= new Date()) {
    const res = await axios({
      method: 'post',
      url: `${config.KIS.vts}${config.KIS.urls.접근토큰발급.path}`,
      headers: { 'content-type': 'application/json' },
      data: {
        grant_type: 'client_credentials',
        appkey: config.KIS.appkey,
        appsecret: config.KIS.appsecret,
      },
    });
    overseaModel.token = res.data;
    console.log('token updated');
  }
  next();
}
