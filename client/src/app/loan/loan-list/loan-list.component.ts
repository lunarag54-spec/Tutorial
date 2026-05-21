import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { forkJoin } from 'rxjs';
import { DialogConfirmationComponent } from '../../core/dialog-confirmation/dialog-confirmation.component';
import { ClientService } from '../../client/client.service';
import { Client } from '../../client/model/client';
import { GameService } from '../../game/game.service';
import { Game } from '../../game/model/game';
import { LoanEditComponent } from '../loan-edit/loan-edit.component';
import { Loan, LoanFilters } from '../model/loan';
import { LoanService } from '../loan.service';

@Component({
    selector: 'app-loan-list',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MatTableModule,
        MatButtonModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatPaginatorModule,
    ],
    templateUrl: './loan-list.component.html',
    styleUrl: './loan-list.component.scss',
})
export class LoanListComponent implements OnInit {
    @ViewChild(MatPaginator) paginator!: MatPaginator;

    displayedColumns: string[] = ['id', 'game', 'client', 'startDate', 'endDate', 'action'];
    loans: Loan[] = [];
    totalElements = 0;
    pageSize = 10;
    pageIndex = 0;

    clients: Client[] = [];
    games: Game[] = [];

    draftGameId: number | null = null;
    draftClientId: number | null = null;
    draftDate: Date | null = null;

    appliedFilters: LoanFilters = {};

    constructor(
        private loanService: LoanService,
        private clientService: ClientService,
        private gameService: GameService,
        private dialog: MatDialog
    ) {}

    ngOnInit(): void {
        this.loadCatalogs();
    }

    loadCatalogs(): void {
        forkJoin({
            clients: this.clientService.getClients(),
            games: this.gameService.getGames(),
        }).subscribe(({ clients, games }) => {
            this.clients = clients;
            this.games = games;
            this.loadLoans();
        });
    }

    loadLoans(): void {
        this.loanService.getLoansPage(this.appliedFilters, this.pageIndex, this.pageSize).subscribe((page) => {
            this.loans = page.content;
            this.totalElements = page.totalElements;
        });
    }

    applyFilters(): void {
        this.appliedFilters = {
            gameId: this.draftGameId,
            clientId: this.draftClientId,
            activeDate: this.draftDate ? this.toApiDate(this.draftDate) : null,
        };
        this.pageIndex = 0;
        if (this.paginator) {
            this.paginator.firstPage();
        }
        this.loadLoans();
    }

    onDateChange(): void {
        this.appliedFilters = {
            ...this.appliedFilters,
            gameId: this.draftGameId,
            clientId: this.draftClientId,
            activeDate: this.draftDate ? this.toApiDate(this.draftDate) : null,
        };
        this.pageIndex = 0;
        if (this.paginator) {
            this.paginator.firstPage();
        }
        this.loadLoans();
    }

    cleanFilters(): void {
        this.draftGameId = null;
        this.draftClientId = null;
        this.draftDate = null;
        this.appliedFilters = {};
        this.pageIndex = 0;
        if (this.paginator) {
            this.paginator.firstPage();
        }
        this.loadLoans();
    }

    onPageChange(event: PageEvent): void {
        this.pageIndex = event.pageIndex;
        this.pageSize = event.pageSize;
        this.loadLoans();
    }

    createLoan(): void {
        this.openLoanDialog();
    }

    editLoan(loan: Loan): void {
        this.openLoanDialog(loan);
    }

    deleteLoan(loan: Loan): void {
        if (loan.id == null) {
            return;
        }

        const dialogRef = this.dialog.open(DialogConfirmationComponent, {
            data: {
                title: 'Eliminar préstamo',
                description: '¿Desea eliminar el préstamo seleccionado?',
            },
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result) {
                this.loanService.deleteLoan(loan.id as number).subscribe(() => {
                    this.loadLoans();
                });
            }
        });
    }

    private openLoanDialog(loan?: Loan): void {
        const dialogRef = this.dialog.open(LoanEditComponent, {
            width: '640px',
            data: {
                loan,
                clients: this.clients,
                games: this.games,
            },
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result) {
                this.loadLoans();
            }
        });
    }

    private toApiDate(date: Date): string {
        const year = date.getFullYear();
        const month = `${date.getMonth() + 1}`.padStart(2, '0');
        const day = `${date.getDate()}`.padStart(2, '0');
        return `${year}-${month}-${day}`;
    }
}
