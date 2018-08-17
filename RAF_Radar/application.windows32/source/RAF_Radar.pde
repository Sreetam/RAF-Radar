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
final float minspeed = 0.08, maxspeed = 0.6; // range of speed
float swipe_angle = 0.0;
final float swipe_speed = 0.01; // how fast the radar swipes

float score = 0.0;

int game_state = 1; // 0 if lost, 1 if we are playing the game, 2 if we won
String str = "";

float fa = 0.0, fb = 0.0; // for radar

class Threat // each enemy that appears is an object. Its easier to deal it that way.
{
  float x = 0.0, y = 0.0, d = 0.0, angle = 0, speed = 0.0, eta = 0.0;

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
  void rad()
  {
    d = sqrt(pow((x - a), 2) + pow((y - b), 2));
  }

  void ang()
  {
    angle = atan((y - b)/(x - a));
    if ((x - a) < 0)
    {
      angle = angle + PI; // to handle rotation because atan()'s range is -pi/2 to pi/2
    }
  }

  void new_coordinate()
  {
    d = d - speed;
    x = d * cos(angle) + a;
    y = d * sin(angle) + b;
  }
  void hasreachedtarget()
  {
    eta = sqrt(pow((a - x), 2) + pow((b - y), 2));
    if (eta<=dead)
    {
      game_state = 0; // game is lost if the enemy reaches your airbase
    }
  }
  void show()
  {
    stroke(200, 0, 0);
    noFill();
    ellipse(x, y, en, en);
  }
}

Threat threat[];

class Missiles
{
  float x = 0.0, y = 0.0, d = 0.0, angle = 0, speed = 0.0;

  boolean isvalid = false;

  Missiles()
  {
    x = a;
    y = b;
    ang();
    speed = 0.05;
    speed = 3;
    d = 0.0;
  }

  void ang()
  {
    angle = atan(( mouseY - b)/( mouseX - a + 0.0001 ));
    if ((mouseX - a) < 0)
    {
      angle = angle + PI;
    }
  }

  void new_coordinate()
  {
    d = d + speed;
    x = d * cos(angle) + a;
    y = d * sin(angle) + b;
  }
  void show()
  {
    noFill();
    stroke(0, 200, 0);
    ellipse(x, y, men, men);
  }
  void isoutofbounds()
  {
    if (d>(circl - 40))
    {
      isvalid =  false; // missiles are lost
      score = score - penalty; // for penalizing losing missiles
    }
  }
  void hasreachedtarget(Threat k)
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

void radarcoordinates()
{
  fa = 0.0;
  fb = 0.0;
  if (swipe_angle>TWO_PI)
  {
    swipe_angle = swipe_angle - TWO_PI; // to prevent an overflow
  }
  fa = circl * cos(swipe_angle) + a;
  fb = circl * sin(swipe_angle) + b;
}
void radarcoordinates(float i)
{
  fa = 0.0;
  fb = 0.0;
  if (swipe_angle>TWO_PI)
  {
    swipe_angle = swipe_angle - TWO_PI;
  }
  fa = circl * cos(swipe_angle - i) + a;
  fb = circl * sin(swipe_angle - i) + b;
}

void swipe()
{

  fa = 0.0;
  fb = 0.0;
  radarcoordinates();
  stroke(0, 255, 0);
  line(a, b, fa, fb);
  float c = 155;
  float i = 0.0;

  for (i =  0; i<0.1; i = i + 0.0003) // the swiping effect. if the swiping effect is increased, fps drops.
  {
    fa = 0.0;
    fb = 0.0;
    radarcoordinates(i);
    stroke(0, c, 0);
    c = c - 0.7;
    line(a, b, fa, fb);
  }
  swipe_angle = swipe_angle + swipe_speed;
}

void refr() // for refreshing the game screen
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

void refr(int i) // refreshing the gamescreen if we won or lost
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

void clearing() // refreshing and creating new enemies
{
  refr();
  score = 0.0;
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

void setup()
{
  size(820, 620);

  clearing();
}

void launchmissiles()
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

void message()
{
  noStroke();
  fill(0, 195, 0);
  textAlign(LEFT);
  textSize(11);
  str = "Point with your cursor and click\nto shoot a missile.\nYou can have only "+ missilelimit +" missiles in air.\nYou will be penalised for losing missiles.\nThe farthest you can aim\nThe higher your score!\n"+ score();
  text(str, 580, 50);
}

String score()
{
  return "Your score is: " + (int)(ceil(score));
}

void missile_vanishes() //missile vanishes if
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

void threatattacks()
{
  for (int i = 0; i<enemylimit; i++)
  {
    if (threat[i].isvalid)
    {
      threat[i].hasreachedtarget(); // checking if any enemy has reached airbase
    }
  }
}

void blipsonradar() // to show who is where
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

void gamescreen() 
{
  refr();
  missile_vanishes();
  threatattacks();
  message();
  blipsonradar();
}

void mousePressed()
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

void keyPressed()
{
  if (game_state == 1 && key=='l')
  {
    launchmissiles();
  }
}

void gameoverscreen() {
  refr(1);
  noStroke();
  fill(255, 0, 0);
  textAlign(CENTER);
  textSize(28);
  text("!!AIRBASE DESTROYED!! \n"+ score() +"\n Click to restart.", a, b);
  message();
}
void gamewinscreen() {
  refr(1);
  noStroke();
  fill(255, 0, 0);
  textAlign(CENTER);
  textSize(28);
  text("!!AIRBASE PROTECTED!! \n"+ score() +"\n click to restart.", a, b);
  message();
}
void draw()
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
