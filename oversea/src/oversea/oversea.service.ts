import { Injectable } from '@nestjs/common';
import config from 'config';
import { HHDFS76410000, makeHeader } from './oversea.type';
import axios from 'axios';
import { overseaModel } from './oversea.model';

@Injectable()
export class OverseaService {
  async revokeP() {}
  async HHDFS76410000(params: HHDFS76410000) {
    return axios({
      method: 'get',
      url: `${config.KIS.vts}${config.KIS.urls.해외주식조건검색.path}`,
      headers: makeHeader({
        tr_id: 'HHDFS76410000',
        authorization: `${overseaModel.token.token_type} ${overseaModel.token.access_token}`,
        custtype: 'P',
      }),
      params: Object.assign({ AUTH: '', EXCD: 'NAS' } as HHDFS76410000, params),
    });
  }
}
