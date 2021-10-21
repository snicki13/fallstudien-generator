import { Component } from '@angular/core'
import { AuthService } from './services/auth.service'
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'web-ui';

  // eslint-disable-next-line no-useless-constructor
  constructor (private auth: AuthService, private route: ActivatedRoute) { }

  ngOnInit (): void {
    this.auth.init(this.route)
  }
}
