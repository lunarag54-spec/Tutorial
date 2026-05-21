import { SortPage } from './SortPage';

export class Pageable {
    pageNumber: number = 0;
    pageSize: number = 10;
    sort: SortPage[] = [];
}
