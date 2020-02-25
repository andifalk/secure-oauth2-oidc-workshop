import { User } from './user';

export class Book {
    identifier: string;
    isbn: string;
    title: string;
    description: string;
    borrowed: boolean;
    authors: string[];
    borrowedBy: User;

    doBorrow(user: User) {
        if (!this.borrowed) {
            this.borrowed = true;
            this.borrowedBy = user;
        }
    }

    returnBookAllowed(user: User): boolean {
        if (!this.borrowed) {
            return false;
        }

        if (user !== null) {
            return this.borrowedBy !== null && this.borrowedBy.username === user.username;
        }

        return false;
    }
}
