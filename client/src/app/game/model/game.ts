import { Author } from "../../author/author";
import { Category } from "../../author/model/category/model/category";

export class Game {
    id: number = 0;
    title: string = '';
    age: number = 0;
    category: Category | null = null;
    author: Author | null = null;
}
