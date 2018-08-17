import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class RAF_Radar extends PApplet {

////////////////////////////////////////////////////////////
//
//*********************RAF RADAR****************************
//
/**
 The game is simple.
 Just point to your enemies and click!
 If they reach you, you are dead.
 
 Developed, designed and crappily made by
 
 Sreetam Ganguly
 
 Released on August 17,2018.
 
 Free to play and open source!
 
 Its written in processing js to make life simple.
 */
//
//**********************************************************
//
////////////////////////////////////////////////////////////
final int wid = 620, heigt = 620;
final int a = wid/2, b = heigt/2;
final int frt = 30, dead = 10, missilelimit = 50, lethal = 10, en = 7, men = 4, penalty = 1;
int circl = 0, missilelog = 0, enemylog = 0, enemylimit = 50;
final float minspeed = 0.08f, maxspeed = 0.6f; // range of speed
float swipe_angle = 0.0f;
final float swipe_speed = 0.01f; // how fast the radar swipes

float score = 0.0f;

int game_state = 1; // 0 if lost, 1 if we are playing the game, 2 if we won
String str = "";

float fa = 0.0f, fb = 0.0f; // for radar

class Threat // each enemy that appears is an object. Its easier to deal it that way.
{
  float x = 0.0f, y = 0.0f, d = 0.0f, angle = 0, speed = 0.0f, eta = 0.0f;

  boolean isvalid = false; // if it is true, it will be shown and will be counted and its coordinates updated

  Threat()
  {
    while (true)
    {
      x = random((a - 2 * circl), (a + 2 * circl));
      y = random ((b - 2 * circl), (b + 2 * circl));
      rad();
      if (d<(circl - 5) && d>(circl - 50))
        break;
    }
    ang();
    speed = random(minspeed, maxspeed);
  }
  public void rad()
  {
    d = sqrt(pow((x - a), 2) + pow((y - b), 2));
  }

  public void ang()
  {
    angle = atan((y - b)/(x - a));
    if ((x - a) < 0)
    {
      angle = angle + PI; // to handle rotation because atan()'s range is -pi/2 to pi/2
    }
  }

  public void new_coordinate()
  {
    d = d - speed;
    x = d * cos(angle) + a;
    y = d * sin(angle) + b;
  }
  public void hasreachedtarget()
  {
    eta = sqrt(pow((a - x), 2) + pow((b - y), 2));
    if (eta<=dead)
    {
      game_state = 0; // game is lost if the enemy reaches your airbase
    }
  }
  public void show()
  {
    stroke(200, 0, 0);
    noFill();
    ellipse(x, y, en, en);
  }
}

Threat threat[];

class Missiles
{
  float x = 0.0f, y = 0.0f, d = 0.0f, angle = 0, speed = 0.0f;

  boolean isvalid = false;

  Missiles()
  {
    x = a;
    y = b;
    ang();
    speed = 0.05f;
    speed = 3;
    d = 0.0f;
  }

  public void ang()
  {
    angle = atan(( mouseY - b)/( mouseX - a + 0.0001f ));
    if ((mouseX - a) < 0)
    {
      angle = angle + PI;
    }
  }

  public void new_coordinate()
  {
    d = d + speed;
    x = d * cos(angle) + a;
    y = d * sin(angle) + b;
  }
  public void show()
  {
    noFill();
    stroke(0, 200, 0);
    ellipse(x, y, men, men);
  }
  public void isoutofbounds()
  {
    if (d>(circl - 40))
    {
      isvalid =  false; // missiles are lost
      score = score - penalty; // for penalizing losing missiles
    }
  }
  public void hasreachedtarget(Threat k)
  {
    float eta = sqrt(pow((k.x - x), 2) + pow((k.y - y), 2));
    if (eta<=lethal)
    {
      k.isvalid = false; // threat and missiles become invalid
      isvalid = false;
      score = score + k.eta/100 + 1;
    }
  }
}

Missiles missiles[];

public void radarcoordinates()
{
  fa = 0.0f;
  fb = 0.0f;
  if (swipe_angle>TWO_PI)
  {
    swipe_angle = swipe_angle - TWO_PI; // to prevent an overflow
  }
  fa = circl * cos(swipe_angle) + a;
  fb = circl * sin(swipe_angle) + b;
}
public void radarcoordinates(float i)
{
  fa = 0.0f;
  fb = 0.0f;
  if (swipe_angle>TWO_PI)
  {
    swipe_angle = swipe_angle - TWO_PI;
  }
  fa = circl * cos(swipe_angle - i) + a;
  fb = circl * sin(swipe_angle - i) + b;
}

public void swipe()
{

  fa = 0.0f;
  fb = 0.0f;
  radarcoordinates();
  stroke(0, 255, 0);
  line(a, b, fa, fb);
  float c = 155;
  float i = 0.0f;

  for (i =  0; i<0.1f; i = i + 0.0003f) // the swiping effect. if the swiping effect is increased, fps drops.
  {
    fa = 0.0f;
    fb = 0.0f;
    radarcoordinates(i);
    stroke(0, c, 0);
    c = c - 0.7f;
    line(a, b, fa, fb);
  }
  swipe_angle = swipe_angle + swipe_speed;
}

public void refr() // for refreshing the game screen
{
  background(0);
  swipe();
  enemylog = 0;
  noStroke();
  fill(0, 255, 0);
  ellipse(a, b, 10, 10);

  stroke(0, 255, 0);
  noFill();
  int i = 0;
  for (i = 1; i <= (min(wid, heigt)/100); i++)
  {
    ellipse(a, b, i * 100, i * 100);
  }
  i = (i - 1) * 50;

  line(a, (b - i), a, (b + i));
  line((a - i), b, (a + i), b);

  circl = i;
}

public void refr(int i) // refreshing the gamescreen if we won or lost
{
  background(0);
  enemylog = 0;
  noStroke();
  fill(0, 255, 0);
  ellipse(a, b, 10, 10);

  stroke(0, 255, 0);
  noFill();

  for (i = 1; i <= (min(wid, heigt)/100); i++)
  {
    ellipse(a, b, i * 100, i * 100);
  }
  i = (i - 1) * 50;

  line(a, (b - i), a, (b + i));
  line((a - i), b, (a + i), b);

  circl = i;
}

public void clearing() // refreshing and creating new enemies
{
  refr();
  score = 0.0f;
  enemylimit = (int)ceil(random(enemylimit - 5, enemylimit + 5));
  threat = new Threat [enemylimit];
  for (int i = 0; i<enemylimit; i++)
  {
    threat [i] = new Threat();
    threat [i].isvalid = true;
  }
  missiles = new Missiles [missilelimit];
  for (int i = 0; i<missilelimit; i++)
  {
    missiles [i] = new Missiles();
  }
}

public void setup()
{
  

  clearing();
}

public void launchmissiles()
{
  if (missilelog<missilelimit)
  {
    for (int i = 0; i<missilelimit; i++)
    {
      if (!missiles[i].isvalid) // creating new objects in place of outdated missiles
      {
        missiles[i] = new Missiles();
        missiles[i].isvalid = true;
        return;
      }
    }
  }
}

public void message()
{
  noStroke();
  fill(0, 195, 0);
  textAlign(LEFT);
  textSize(11);
  str = "Point with your cursor and click\nto shoot a missile.\nYou can have only "+ missilelimit +" missiles in air.\nYou will be penalised for losing missiles.\nThe farthest you can aim\nThe higher your score!\n"+ score();
  text(str, 580, 50);
}

public String score()
{
  return "Your score is: " + (int)(ceil(score));
}

public void missile_vanishes() //missile vanishes if
{
  for (int i = 0; i<missilelimit; i++)
  {
    if (missiles[i].isvalid)
    {
      missiles[i].isoutofbounds(); // it moves too far
    }
  }
  for (int i = 0; i<enemylimit; i++)
  {
    for (int j = 0; j<missilelimit; j++)
    {
      if (threat[i].isvalid && missiles[j].isvalid)
      {
        missiles[j].hasreachedtarget(threat[i]); // or has reached its target
      }
    }
  }
}

public void threatattacks()
{
  for (int i = 0; i<enemylimit; i++)
  {
    if (threat[i].isvalid)
    {
      threat[i].hasreachedtarget(); // checking if any enemy has reached airbase
    }
  }
}

public void blipsonradar() // to show who is where
{
  for (int i = 0; i<enemylimit; i++)
  {
    if (threat[i].isvalid)
    {
      threat[i].show();
      threat[i].new_coordinate();
      enemylog++;
    }
  }
  if (enemylog == 0) // if no enemy remains
  {
    game_state = 2; // game won
    return;
  }
  for (int i = 0; i<missilelimit; i++)
  {
    if (missiles[i].isvalid)
    {
      missiles[i].show();
      missiles[i].new_coordinate();
    }
  }
}

public void gamescreen() 
{
  refr();
  missile_vanishes();
  threatattacks();
  message();
  blipsonradar();
}

public void mousePressed()
{
  if (game_state == 1)
  {
    launchmissiles();
  } else if (game_state == 0||game_state == 2) // game over or game win screen
  {
    game_state = 1;
    clearing();
  }
}

public void keyPressed()
{
  if (game_state == 1 && key=='l')
  {
    launchmissiles();
  }
}

public void gameoverscreen() {
  refr(1);
  noStroke();
  fill(255, 0, 0);
  textAlign(CENTER);
  textSize(28);
  text("!!AIRBASE DESTROYED!! \n"+ score() +"\n Click to restart.", a, b);
  message();
}
public void gamewinscreen() {
  refr(1);
  noStroke();
  fill(255, 0, 0);
  textAlign(CENTER);
  textSize(28);
  text("!!AIRBASE PROTECTED!! \n"+ score() +"\n click to restart.", a, b);
  message();
}
public void draw()
{
  frameRate(frt);

  if (game_state == 1)
  {
    gamescreen();
  } else if (game_state == 0)
  {
    gameoverscreen();
  } else if (game_state == 2)
  {
    gamewinscreen();
  }
}
  public void settings() {  size(820, 620); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "RAF_Radar" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
