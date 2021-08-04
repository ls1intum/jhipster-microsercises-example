import * as dayjs from 'dayjs';

export interface ILecture {
  id?: number;
  title?: string | null;
  description?: string | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
}

export class Lecture implements ILecture {
  constructor(
    public id?: number,
    public title?: string | null,
    public description?: string | null,
    public startDate?: dayjs.Dayjs | null,
    public endDate?: dayjs.Dayjs | null
  ) {}
}

export function getLectureIdentifier(lecture: ILecture): number | undefined {
  return lecture.id;
}
