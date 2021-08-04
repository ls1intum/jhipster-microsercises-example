import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILecture, getLectureIdentifier } from '../lecture.model';

export type EntityResponseType = HttpResponse<ILecture>;
export type EntityArrayResponseType = HttpResponse<ILecture[]>;

@Injectable({ providedIn: 'root' })
export class LectureService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/lectures', 'lecture');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(lecture: ILecture): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lecture);
    return this.http
      .post<ILecture>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(lecture: ILecture): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lecture);
    return this.http
      .put<ILecture>(`${this.resourceUrl}/${getLectureIdentifier(lecture) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(lecture: ILecture): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lecture);
    return this.http
      .patch<ILecture>(`${this.resourceUrl}/${getLectureIdentifier(lecture) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ILecture>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILecture[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLectureToCollectionIfMissing(lectureCollection: ILecture[], ...lecturesToCheck: (ILecture | null | undefined)[]): ILecture[] {
    const lectures: ILecture[] = lecturesToCheck.filter(isPresent);
    if (lectures.length > 0) {
      const lectureCollectionIdentifiers = lectureCollection.map(lectureItem => getLectureIdentifier(lectureItem)!);
      const lecturesToAdd = lectures.filter(lectureItem => {
        const lectureIdentifier = getLectureIdentifier(lectureItem);
        if (lectureIdentifier == null || lectureCollectionIdentifiers.includes(lectureIdentifier)) {
          return false;
        }
        lectureCollectionIdentifiers.push(lectureIdentifier);
        return true;
      });
      return [...lecturesToAdd, ...lectureCollection];
    }
    return lectureCollection;
  }

  protected convertDateFromClient(lecture: ILecture): ILecture {
    return Object.assign({}, lecture, {
      startDate: lecture.startDate?.isValid() ? lecture.startDate.toJSON() : undefined,
      endDate: lecture.endDate?.isValid() ? lecture.endDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.startDate = res.body.startDate ? dayjs(res.body.startDate) : undefined;
      res.body.endDate = res.body.endDate ? dayjs(res.body.endDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((lecture: ILecture) => {
        lecture.startDate = lecture.startDate ? dayjs(lecture.startDate) : undefined;
        lecture.endDate = lecture.endDate ? dayjs(lecture.endDate) : undefined;
      });
    }
    return res;
  }
}
