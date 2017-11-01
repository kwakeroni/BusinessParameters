import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { EntryTableComponent} from './entryTable.component';
import { ParametersService } from './parameters.service';

@NgModule({
  declarations: [
    AppComponent,
    EntryTableComponent
  ],
  imports: [
    BrowserModule,
    FormsModule
  ],
  providers: [
    ParametersService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
