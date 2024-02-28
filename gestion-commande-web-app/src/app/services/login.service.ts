import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private apiUrl = 'http://localhost:2222/login';
  private getUserByEmailUrl = 'http://localhost:2222/login/getUserByEmail';

  constructor(private httpClient: HttpClient) {}

  login(email: string, password: string): Observable<any> {
    const loginData = new URLSearchParams();
    loginData.set('email', email);
    loginData.set('password', password);

    return this.httpClient.post(this.apiUrl, loginData.toString(), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      responseType: 'text'
    }).pipe(
      catchError((error: HttpErrorResponse) => {
        // Handle errors appropriately
        console.error('Server error:', error);
        return throwError('Login failed. Please try again.'); // You can customize the error message
      })
    );
  }

  getUserIdByEmail(email: string): Observable<number> {
    return this.httpClient.get<number>(`${this.getUserByEmailUrl}?email=${email}`).pipe(
      catchError((error: HttpErrorResponse) => {
        // Handle errors appropriately
        console.error('Server error:', error);
        return throwError('Error getting user ID by email.'); // You can customize the error message
      })
    );
  }

  updatePassword(id: number, updatedProfesseur: any): Observable<any> {
    return this.httpClient.put(`${this.apiUrl}/${id}`, updatedProfesseur).pipe(
      catchError((error: HttpErrorResponse) => {
        // Handle errors appropriately
        console.error('Server error:', error);
        return throwError('Error updating password.'); // You can customize the error message
      })
    );
  }

  isSamePassword(formPassword: string, oldPasswordList: string[]): Observable<string> {
    return this.httpClient.get<string>(`http://localhost:1818/PROFESSOR-SERVICE/login/isSamePassword/${formPassword}/${oldPasswordList[0]}`);
  }

}
