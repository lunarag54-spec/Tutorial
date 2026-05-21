import { HttpErrorResponse } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ClientService } from '../client.service';
import { Client } from '../model/client';

@Component({
    selector: 'app-client-edit',
    standalone: true,
    imports: [FormsModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
    templateUrl: './client-edit.component.html',
    styleUrl: './client-edit.component.scss',
})
export class ClientEditComponent implements OnInit {
    client: Client = { name: '' };
    duplicateNameError = false;

    constructor(
        public dialogRef: MatDialogRef<ClientEditComponent>,
        @Inject(MAT_DIALOG_DATA) public data: { client?: Client },
        private clientService: ClientService
    ) {}

    ngOnInit(): void {
        this.client = this.data.client
            ? { ...this.data.client }
            : { name: '' };
    }

    onNameChange(): void {
        this.duplicateNameError = false;
    }

    onSave(): void {
        this.duplicateNameError = false;

        this.clientService.saveClient(this.client).subscribe({
            next: () => this.dialogRef.close(this.client),
            error: (error: HttpErrorResponse) => {
                const message = `${error.error?.message ?? ''}`.toLowerCase();
                this.duplicateNameError = error.status === 409 || message.includes('exists') || message.includes('existe');
            },
        });
    }

    onClose(): void {
        this.dialogRef.close();
    }
}
