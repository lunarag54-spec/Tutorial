import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { DialogConfirmationComponent } from '../../core/dialog-confirmation/dialog-confirmation.component';
import { ClientService } from '../client.service';
import { Client } from '../model/client';
import { ClientEditComponent } from '../client-edit/client-edit.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
    selector: 'app-client-list',
    standalone: true,
    imports: [MatButtonModule, MatIconModule, MatTableModule, CommonModule, MatSnackBarModule],
    templateUrl: './client-list.component.html',
    styleUrl: './client-list.component.scss',
})
export class ClientListComponent implements OnInit {
    dataSource = new MatTableDataSource<Client>();
    displayedColumns: string[] = ['id', 'name', 'action'];

    constructor(
        private clientService: ClientService,
        public dialog: MatDialog,
        private snackBar: MatSnackBar
    ) {}

    ngOnInit(): void {
        this.loadClients();
    }

    loadClients(): void {
        this.clientService.getClients().subscribe((clients) => {
            this.dataSource.data = clients;
        });
    }

    createClient(): void {
        const dialogRef = this.dialog.open(ClientEditComponent, {
            data: {},
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result) {
                this.loadClients();
            }
        });
    }

    editClient(client: Client): void {
        const dialogRef = this.dialog.open(ClientEditComponent, {
            data: { client },
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result) {
                this.loadClients();
            }
        });
    }

    deleteClient(client: Client): void {
        if (client.id == null) {
            return;
        }

        const dialogRef = this.dialog.open(DialogConfirmationComponent, {
            data: {
                title: 'Eliminar cliente',
                description:
                    'Atención si borra el cliente se perderán sus datos.<br> ¿Desea eliminar el cliente?',
            },
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result) {
                this.clientService.deleteClient(client.id).subscribe({
                    next: () => {
                        this.loadClients();
                    },
                    error: (err) => {
                        this.snackBar.open(err.error?.message || 'Ha ocurrido un error al eliminar el cliente.', 'Cerrar', {
                            duration: 5000,
                        });
                    }
                });
            }
        });
    }
}
