import { Pageable } from "./Pageable";
import { Author } from "../../../../app/author/author";

export class AuthorPage {
    content: Author[] = [];
    pageable: Pageable = new Pageable();
    totalElements: number = 0;
}
