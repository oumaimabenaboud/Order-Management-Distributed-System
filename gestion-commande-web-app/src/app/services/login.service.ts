import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private apiUrl = 'http://localhost:2222/login';

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
  
}