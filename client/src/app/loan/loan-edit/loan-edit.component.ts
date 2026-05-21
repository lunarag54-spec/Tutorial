import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { environment } from '../../../environments/environment';
import { Client } from '../../client/model/client';
import { Game } from '../../game/model/game';
import { Loan } from '../model/loan';
import { LoanService } from '../loan.service';

interface LoanEditDialogData {
    loan?: Loan;
    clients: Client[];
    games: Game[];
}

@Component({
    selector: 'app-loan-edit',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatSelectModule,
        MatDatepickerModule,
        MatNativeDateModule,
    ],
    templateUrl: './loan-edit.component.html',
    styleUrl: './loan-edit.component.scss',
})
export class LoanEditComponent implements OnInit {
    private readonly maxLoanDurationDays = environment.maxLoanDurationDays;

    loan: Loan = {
        client: null,
        game: null,
        startDate: '',
        endDate: '',
    };
    clients: Client[] = [];
    games: Game[] = [];

    startDate: Date | null = null;
    endDate: Date | null = null;

    backendError = '';
    validationErrors: string[] = [];
    durationError = '';
    saveAttempted = false;

    constructor(
        public dialogRef: MatDialogRef<LoanEditComponent>,
        @Inject(MAT_DIALOG_DATA) public data: LoanEditDialogData,
        private loanService: LoanService
    ) {}

    ngOnInit(): void {
        this.clients = this.data.clients;
        this.games = this.data.games;
        this.loan = this.data.loan
            ? { ...this.data.loan, client: this.data.loan.client, game: this.data.loan.game }
            : {
                  client: null,
                  game: null,
                  startDate: '',
                  endDate: '',
              };

        this.startDate = this.parseDate(this.loan.startDate);
        this.endDate = this.parseDate(this.loan.endDate);
        this.validateDatesRealtime();
    }

    onDateChange(): void {
        this.backendError = '';
        this.durationError = '';
        this.validateDatesRealtime();
    }

    onSelectionChange(): void {
        this.backendError = '';
    }

    onSave(): void {
        this.saveAttempted = true;
        this.backendError = '';
        this.durationError = '';
        this.validationErrors = [];

        if (!this.loan.client?.id || !this.loan.game?.id || !this.startDate || !this.endDate) {
            this.validationErrors.push('Complete todos los campos obligatorios.');
            return;
        }

        if (this.endDate < this.startDate) {
            this.validationErrors.push('La fecha de fin no puede ser anterior a la fecha de inicio.');
            return;
        }

        const duration = this.getDateDifferenceInDays(this.startDate, this.endDate);
        if (duration > this.maxLoanDurationDays) {
            this.durationError = `El rango de fechas no puede superar los ${this.maxLoanDurationDays} días naturales.`;
            return;
        }

        const payload: Loan = {
            ...this.loan,
            startDate: this.toApiDate(this.startDate),
            endDate: this.toApiDate(this.endDate),
        };

        this.loanService.saveLoan(payload).subscribe({
            next: (savedLoan) => this.dialogRef.close(savedLoan),
            error: (error: HttpErrorResponse) => {
                const code = `${error.error?.code ?? ''}`;
                const message = `${error.error?.message ?? ''}`.toLowerCase();

                if (code === 'LOAN_GAME_NOT_AVAILABLE' || message.includes('solap') || message.includes('disponible')) {
                    this.backendError = 'No se pudo guardar: el juego ya está prestado en el rango de fechas seleccionado.';
                    return;
                }

                if (code === 'LOAN_CLIENT_MAX_ACTIVE_GAMES' || message.includes('límite') || message.includes('limite')) {
                    this.backendError =
                        'No se pudo guardar: el cliente supera su límite de préstamos para el rango seleccionado.';
                    return;
                }

                if (code === 'LOAN_DURATION_EXCEEDS_MAX') {
                    this.durationError = `El rango de fechas no puede superar los ${this.maxLoanDurationDays} días naturales.`;
                    return;
                }

                if (code === 'LOAN_DATES_INCONSISTENT') {
                    this.validationErrors.push('La fecha de fin no puede ser anterior a la fecha de inicio.');
                    return;
                }

                this.backendError = error.error?.message ?? 'No se pudo guardar el préstamo. Verifique los datos e intente nuevamente.';
            },
        });
    }

    onClose(): void {
        this.dialogRef.close();
    }

    get canSave(): boolean {
        return (
            !!this.loan.client?.id &&
            !!this.loan.game?.id &&
            !!this.startDate &&
            !!this.endDate &&
            this.validationErrors.length === 0
        );
    }

    private validateDatesRealtime(): void {
        this.validationErrors = [];

        if (!this.startDate || !this.endDate) {
            return;
        }

        if (this.endDate < this.startDate) {
            this.validationErrors.push('La fecha de fin no puede ser anterior a la fecha de inicio.');
        }
    }

    private getDateDifferenceInDays(startDate: Date, endDate: Date): number {
        const start = this.toStartOfDay(startDate).getTime();
        const end = this.toStartOfDay(endDate).getTime();
        return Math.floor((end - start) / (1000 * 60 * 60 * 24)) + 1;
    }

    private parseDate(value: string): Date | null {
        if (!value) {
            return null;
        }

        const parsedDate = new Date(value);
        return Number.isNaN(parsedDate.getTime()) ? null : this.toStartOfDay(parsedDate);
    }

    private toApiDate(date: Date): string {
        const year = date.getFullYear();
        const month = `${date.getMonth() + 1}`.padStart(2, '0');
        const day = `${date.getDate()}`.padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    private toStartOfDay(date: Date): Date {
        return new Date(date.getFullYear(), date.getMonth(), date.getDate());
    }
}
