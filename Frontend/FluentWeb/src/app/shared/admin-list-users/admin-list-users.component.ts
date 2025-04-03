import {Component, HostListener, OnInit} from '@angular/core';
import {AdminListUsersService, UserDetails} from '../../services/admin-list-users.service';
import {Page} from '../../model/page.model';
import {NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-admin-list-users',
  imports: [
    NgForOf,
    FormsModule,
    NgIf
  ],
  templateUrl: './admin-list-users.component.html',
  styleUrl: './admin-list-users.component.css'
})
export class AdminListUsersComponent implements OnInit{
  users: UserDetails[] = [];
  totalPages: number = 0;
  pageSize: number = 2;
  currentPage: number = 0;
  inputValue: number = 1;

  // Variable to editing
  editingRowId: number | null = null;
  roles: string[] = ['USER', 'ADMIN', 'SYSTEM'];
  role: string = '';
  originalUser: UserDetails = {} as UserDetails;
  tempUser: UserDetails = {} as UserDetails;

  constructor(private adminListUsers: AdminListUsersService) { }

  ngOnInit(): void {
    this.loadUsers(0);
  }

  @HostListener('document:keydown', ['$event'])
  handleKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      if (this.inputValue - 1 < this.totalPages && this.inputValue - 1 >= 0) {
        this.loadUsers(this.inputValue - 1);
      }
    }
  }

  loadUsers(page: number) {
    this.adminListUsers.getUsers(page, this.pageSize).subscribe((response: Page<UserDetails>) => {
      this.users = response.content;
      this.totalPages = response.totalPages;
      this.currentPage = response.number;
    });
  }

  nextPage() {
    if (this.currentPage + 1 < this.totalPages) {
      this.loadUsers(this.currentPage + 1);
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.loadUsers(this.currentPage - 1);
    }
  }

  saveEditing() {
    const updatedFields: Partial<UserDetails> = {};

    if (this.tempUser.name !== this.originalUser.name) {
      updatedFields.name = this.tempUser.name;
    }

    if (this.tempUser.surname !== this.originalUser.surname) {
      updatedFields.surname = this.tempUser.surname;
    }

    if (this.tempUser.role !== this.originalUser.role) {
      updatedFields.role = this.tempUser.role;
    }

    console.log(this.tempUser);
    console.log(this.originalUser);
    console.log("Changing data...", updatedFields);

    if (Object.keys(updatedFields).length > 0) {
      this.adminListUsers.patchUser(updatedFields, this.originalUser.userId).subscribe({
        next: (response) => {
          console.log("Successfully updated user", response);

          const index = this.users.findIndex(u => u.userId === this.originalUser.userId);
          if (index !== -1) {
            this.users[index] = { ...this.users[index], ...updatedFields };
          }

          this.cancelEditing();
        },
        error: (err) => {
          console.error("Error while changing user's data ", err)
        }
      });
    }
  }

  cancelEditing() {
    this.editingRowId = null;
    this.tempUser = {} as UserDetails;
    this.originalUser = {} as UserDetails;
  }

  startEditing(user: UserDetails) {
    this.editingRowId = user.userId;
    this.tempUser = { ...user };
    this.originalUser = { ...user };
    this.role = user.role;
    console.log("Edytuje ", this.editingRowId);
  }
}
