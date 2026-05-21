import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AuthorEditComponent } from '../author-edit/author-edit.component';
import { AuthorService } from '../author.service';
import { Author } from '../author';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { DialogConfirmationComponent } from '../../core/dialog-confirmation/dialog-confirmation.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
    selector: 'app-author-list',
    standalone: true,
    imports: [
        MatButtonModule,
        MatIconModule,
        MatTableModule,
        CommonModule,
        MatSnackBarModule
    ],
    templateUrl: './author-list.component.html',
    styleUrl: './author-list.component.scss',
})
export class AuthorListComponent implements OnInit {
    authors: Author[] = [];
    displayedColumns: string[] = ['id', 'name', 'nationality', 'actions'];

    constructor(
        private authorService: AuthorService,
        public dialog: MatDialog,
        private snackBar: MatSnackBar
    ) {}

    ngOnInit(): void {
        this.loadAuthors();
    }

    loadAuthors() {
        this.authorService.getAllAuthors().subscribe((authors) => {
            this.authors = authors;
        });
    }

    editAuthor(author: Author) {
        const dialogRef = this.dialog.open(AuthorEditComponent, {
            data: { author },
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result) {
                this.loadAuthors();
            }
        });
    }

    deleteAuthor(id: number) {
        const dialogRef = this.dialog.open(DialogConfirmationComponent, {
            data: {
                title: 'Eliminar autor',
                description:
                    'Atención si borra el autor se perderán sus datos.<br> ¿Desea eliminar el autor?',
            },
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result) {
                this.authorService.deleteAuthor(id).subscribe({
                    next: () => {
                        this.loadAuthors();
                    },
                    error: (err) => {
                        this.snackBar.open(err.error?.message || 'Ha ocurrido un error al eliminar el autor.', 'Cerrar', {
                            duration: 5000,
                        });
                    }
                });
            }
        });
    }

    createAuthor() {
        const dialogRef = this.dialog.open(AuthorEditComponent, {
            data: {},
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result) {
                this.loadAuthors();
            }
        });
    }
}
