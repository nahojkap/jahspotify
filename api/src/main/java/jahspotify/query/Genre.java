package jahspotify.query;

/**
 * @author Johan Lindquist
 */
public enum Genre
{
    TWO_STEP_BRITISH_GARAGE("2-Step/British Garage"),
    ABORIGINAL_FOLK("Aboriginal Folk");

    private String _textual;

    Genre(final String textual)
    {
        _textual = textual;
    }
/*
    2-Step/British Garage
    Aboriginal Rock

    Acadian
    Acappella
    Acid Folk
    Acid House
    Acid Jazz
    Acid Rock
    Acid Techno
    Acoustic Blues
    Acoustic Chicago Blues
    Acoustic Louisiana Blues
    Acoustic Memphis Blues
    Acoustic New Orleans Blues
    Acoustic Texas Blues
    Adult Alternative
    Adult Alternative Pop/Rock
    Adult Contemporary
    African Folk
    African Jazz
    Afro-Beat
    Afro-Brazilian
    Afro-Colombian
    Afro-Cuban
    Afro-Cuban Jazz
    Afro Peruvian
    Afro-Pop
    Album Rock
    Al-Jil
    Alternative CCM
    Alternative Country
    Alternative Country-Rock
    Alternative Dance
    Alternative Folk
    Alternative Metal
    Alternative Pop/Rock
    Alternative Rap
    Alternative Singer/Songwriter
    Ambient
    Ambient Breakbeat
    Ambient Dub
    Ambient House
    Ambient Pop
    Ambient Techno
    Americana
    American Jewish Pop
    American Popular Song
    American Punk
    American Trad Rock
    American Underground
    AM Pop
    Anarchist Punk
    Andalus Classical
    Andean Folk
    Anime Music
    Anti-Folk
    Apala
    Appalachian Folk
    Arena Rock
    Argentine Folk
    Armenian
    Armenian Folk
    Asian Folk
    Asian Pop
    Atonal
    Audiobooks
    Aussie Rock
    AustroPop
    Avant-Garde
    Avant-Garde Jazz
    Avant-Prog
    Bachata
    Bakersfield Sound
    Ballads
    Ballroom Dance
    Banda
    Bar Band
    Barbershop Quartet
    Baroque
    Baroque Pop
    Bass Music
    Bava
    Beach
    Beat Poetry
    Beguine
    Beguine Moderne
    Beguine Vide
    Belair
    Bel Canto Opera
    Belly Dancing
    Bhangra
    Big Band
    Big Band Latino
    Big Beat
    Bikutsi
    Black Gospel
    Blaxploitation
    Bluebeat
    Blue-Eyed Soul
    Bluegrass
    Bluegrass-Gospel
    Blue Humor
    Blues
    Blues Gospel
    Blues Revival
    Blues-Rock
    Bolero
    Bollywood
    Bombara
    Boogaloo
    Boogie Rock
    Boogie-Woogie
    Bop
    Bossa Nova
    Brass Band
    Brazilian Folk
    Brazilian Jazz
    Brazilian Pop
    Brill Building Pop
    British Blues
    British Dance Bands
    British Folk
    British Folk-Rock
    British Invasion
    British Metal
    British Psychedelia
    British Punk
    British Rap
    British Trad Rock
    Britpop
    Broken Beat
    Brown-Eyed Soul
    Bubblegum
    Bulgarian Folk
    C-86
    Cabaret
    Cadence
    Cajun
    Calypso
    Canterbury Scene
    Caribbean Folk
    Carnatic
    Carnival
    Carols
    Cartoon Music
    Cast Recordings
    CCM
    Celebrity
    Celtic
    Celtic Folk
    Celtic Fusion
    Celtic Gospel
    Celtic New Age
    Celtic Pop
    Celtic Rock
    Ceremonial
    Cha-Cha
    Chamber Jazz
    Chamber Music
    Chamber Pop
    Changui
    Chant
    Chants
    Chanukah
    Charanga
    Chassidic
    Chicago Blues
    Chicago Soul
    Children's
    Children's Folk
    Children's Stories
    Chilean Folk
    Chimurenga
    Chinese Classical
    Chinese Folk
    Chinese Pop
    Chinese Pop-Rock
    Choral
    Choro
    Chouval Bwa
    Christian Comedy
    Christian Metal
    Christian Punk
    Christian Rap
    Christian Rock
    Christmas
    Circus Music
    Classical
    Classical Crossover
    Classical Guitar
    Classical Pop
    Classic Female Blues
    Classic Jazz
    Close Harmony
    Club/Dance
    Cocktail
    College Rock
    Comedy
    Comedy Rap
    Comedy Rock
    Compas
    Computer Music
    Conceptual Art
    Conjunto
    Contemporary Bluegrass
    Contemporary Blues
    Contemporary Celtic
    Contemporary Country
    Contemporary Flamenco
    Contemporary Folk
    Contemporary Gospel
    Contemporary Instrumental
    Contemporary Jazz
    Contemporary Native American
    Contemporary R&B
    Contemporary Reggae
    Contemporary Singer/Songwriter
    Continental Jazz
    Cool
    Corrido
    Country
    Country Blues
    Country Boogie
    Country Comedy
    Country-Folk
    Country Gospel
    Country-Pop
    Country-Rock
    Country-Soul
    Cowboy
    Cowpunk
    Creative Orchestra
    Creole
    Crossover Jazz
    Cuatro
    Cuban Jazz
    Cuban Pop
    Cumbia
    Dance Bands
    Dancehall
    Dance-Pop
    Dance-Rock
    Danzon
    Dark Ambient
    Death Metal/Black Metal
    Deep Funk
    Deep Funk Revival
    Deep Soul
    Delta Blues
    Demonstration/Test Disc
    Detroit Blues
    Detroit Rock
    Detroit Techno
    Dhrupad
    Dimotiko
    Dirty Blues
    Dirty Rap
    Dirty South
    Disco
    Dixieland
    Dixieland Revival
    DJ
    Djabdong
    Documentary
    Doom Metal
    Doo Wop
    Downbeat
    Downtempo
    Drama
    Dream Pop
    Drill'n'bass
    Drinking Songs
    Dub
    Dub Poetry
    Dutch Pop
    Early American Blues
    Early British Pop/Rock
    Early Creative
    Early Music
    East Coast Blues
    East Coast Rap
    Easter
    Eastern Europe
    Eastern European Pop
    Easy Listening
    Easy Pop
    Educational
    Egyptian/Sudanese Music
    Electric Blues
    Electric Chicago Blues
    Electric Country Blues
    Electric Delta Blues
    Electric Harmonica Blues
    Electric Memphis Blues
    Electric Texas Blues
    Electro
    Electro-Acoustic
    Electro-Industrial
    Electro-Jazz
    Electronic
    Electronica
    Electro-Techno
    Emo
    Enka
    Environmental
    Erotica
    Ethiopian Pop
    Ethnic
    Ethnic Comedy
    Ethnic Fusion
    Euro-Dance
    European Folk
    Euro-Pop
    Euro-Rock
    Exercise
    Exotica
    Experimental
    Experimental Ambient
    Experimental Big Band
    Experimental Dub
    Experimental Electro
    Experimental Jungle
    Experimental Rock
    Experimental Techno
    Fado
    Fairy Tales
    Fantasy
    Field Recordings
    Fight Songs
    Film Music
    Finger-Picked Guitar
    Finnish Folk
    Flamenco
    Folk
    Folk-Blues
    Folk-Jazz
    Folklore
    Folk-Pop
    Folk Revival
    Folk-Rock
    Folksongs
    Foreign Language Rap
    Foreign Language Rock
    Forro
    Frat Rock
    Freakbeat
    Free Folk
    Free Funk
    Free Improvisation
    Free Jazz
    Freestyle
    French Chanson
    French Folk
    French Pop
    French Rock
    Fuji
    Funk
    Funk Metal
    Funky Breaks
    Fusion
    Gabba
    Gamelan
    Game Sounds
    Gangsta Rap
    Garage/House
    Garage Punk
    Garage Rap/Grime
    Garage Rock
    Garage Rock Revival
    Gay
    Gay Comedy
    Gay Country
    Gay Gospel
    Georgian Choir
    German Liedermacher
    German Schlager
    German Volksmusik
    G-Funk
    Giddha
    Girl Group
    Glam Rock
    Glitch
    Glitter
    Goa Trance
    Go-Go
    Golden Age
    Gospel
    Gospel Choir
    Goth Metal
    Goth Rock
    Greek Folk
    Gregorian Chant
    Grindcore
    Grunge
    Grupero
    Guaguanc√≥
    Guitar Virtuoso
    Gwo Ka
    Gypsy
    Hair Metal
    Halloween
    Happy Hardcore
    Hard Bop
    Hardcore Punk
    Hardcore Rap
    Hardcore Techno
    Hard Rock
    Harmonica Blues
    Harmony Vocal Group
    Hawaiian Pop
    Healing
    Heartland Rock
    Heavy Metal
    Hebrew
    Highlife
    Hi-NRG
    Hip-Hop
    Holiday
    Hong Kong Pop
    Honky Tonk
    Hot Jazz
    Hot Rod
    Hot Rod Revival
    House
    Hungarian Folk
    Hymns
    IDM
    Illbient
    Impressionist
    Improvisation
    Indian Classical
    Indian Folk
    Indian Pop
    Indie Electronic
    Indie Pop
    Indie Rock
    Indigenous
    Indipop
    Industrial
    Industrial Dance
    Industrial Drum'n'Bass
    Industrial Metal
    Inspirational
    Instructional
    Instrumental Country
    Instrumental Gospel
    Instrumental Pop
    Instrumental Rock
    International Folk
    International Pop
    Interview
    Inuit
    Iran-Classical
    Irish Folk
    Italian Folk
    Italian Pop
    Jaipongan
    Jam Bands
    Jangle Pop
    Japanese Orchestral
    Japanese Pop
    Japanese Rock
    Jazz
    Jazz Blues
    Jazz-Funk
    Jazz-House
    Jazz-Pop
    Jazz-Rap
    Jazz-Rock
    Jesus Rock
    Jewish Folk
    Jewish Music
    Jibaro
    Jit
    Jive
    Joik
    Jug Band
    Juju
    Juke Joint Blues
    Jump Blues
    Jungle/Drum'n'bass
    Junkanoo
    Kabuki
    Karaoke
    Kayokyoku
    Kecak
    Khmer Dance
    Klezmer
    Kora
    Korean Pop
    Korean Rock
    Kraut Rock
    Laika
    Lambada
    L.A. Punk
    Latin
    Latin CCM
    Latin Comedy
    Latin Dance
    Latin Folk
    Latin Freestyle
    Latin Gospel
    Latin Jazz
    Latin Pop
    Latin Rap
    Latin Rock
    Latin Soul
    Left-Field Hip-Hop
    Left-Field House
    Lo-Fi
    Louisiana Blues
    Lounge
    Lovers Rock
    Lullabies
    Macapat Poetry
    Madchester
    Mainstream Jazz
    Makossa
    Mambo
    Mantras
    Marabi
    March
    Marches
    Mariachi
    Math Rock
    Mbalax
    Mbaqanga
    M-Base
    Mbira
    Mbube
    Mbuti Choral
    Medieval
    Meditation
    Memphis Blues
    Memphis Soul
    Mento
    Merengue
    Merenhouse
    Merseybeat
    Mexican Folk
    Microhouse
    Microsound
    Microtonal
    Middle Eastern Pop
    Midwest Rap
    Military
    Mini Jazz
    Minimalism
    Minimal Techno
    Minstrel
    Miscellaneous
    Mixed Media
    Mod
    Modal Music
    Modern Acoustic Blues
    Modern Big Band
    Modern Composition
    Modern Creative
    Modern Delta Blues
    Modern Electric Blues
    Modern Electric Chicago Blues
    Modern Electric Texas Blues
    Modern Free
    Modern Son
    Mod Revival
    Mood Music
    Moravian Folk
    Morna
    Morning Radio
    Motown
    Movie Themes
    MPB
    Mugam
    Musette
    Musical Comedy
    Musicals
    Musical Theater
    Music Hall
    Musique Actuelle
    Musique Concr√®te
    Mystical Minimalism
    Nashville Sound/Countrypolitan
    Nationalist
    Nature
    Neo-Bop
    Neo-Classical
    Neo-Classical Metal
    Neo-Electro
    Neo-Glam
    Neo-Prog
    Neo-Psychedelia
    Neo-Romantic
    Neo-Soul
    Neo-Traditional
    Neo-Traditional Folk
    Neo-Traditionalist Country
    New Acoustic
    New Age
    New Age Tone Poems
    Newbeat
    New Jack Swing
    New Orleans Blues
    New Orleans Brass Bands
    New Orleans Jazz
    New Orleans R&B
    New Romantic
    New Traditionalist
    New Wave
    New Wave of British Heavy Metal
    New Wave/Post-Punk Revival
    New York Blues
    New York Punk
    New York Salsa
    New Zealand Rock
    Nissiotiko
    Noh
    Noise
    Noise Pop
    Noise-Rock
    Norte√±o
    Northern Soul
    Norwegian Folk
    Nostalgia
    Novelty
    Novelty Ragtime
    No Wave
    Nu Breaks
    Nueva Cancion
    Nueva Trova
    Nursery Rhymes
    Nyahbinghi
    Obscuro
    Observational Humor
    Oi!
    Okinawan Pop
    Okinawan Traditional
    Old-School Rap
    Old-Timey
    Omutibo
    Onda Grupera
    Opera
    Operettas
    Oratories
    Orchestral
    Orchestral Jazz
    Orchestral Pop
    Original Score
    Outlaw Country
    Pachanga
    Paisley Underground
    Pan-Global
    Party Rap
    Party Soca
    Peruvian Folk
    Philly Soul
    Piano Blues
    Piedmont Blues
    Pipe Bands
    Plena
    Poetry
    Political Comedy
    Political Folk
    Political Rap
    Political Reggae
    Polka
    Pop
    Pop Idol
    Pop-Metal
    Pop-Rap
    Pop/Rock
    Pop-Soul
    Pop Underground
    Portuguese Music
    Post-Bop
    Post-Disco
    Post-Grunge
    Post-Hardcore
    Post-Minimalism
    Post-Punk
    Post-Rock/Experimental
    Post-Romantic
    Power Metal
    Power Pop
    Praise & Worship
    Prank Calls
    Prewar Blues
    Prewar Country Blues
    Prewar Gospel Blues
    Process-Generated
    Progressive Alternative
    Progressive Big Band
    Progressive Bluegrass
    Progressive Country
    Progressive Electronic
    Progressive Folk
    Progressive House
    Progressive Jazz
    Progressive Metal
    Progressive Trance
    Prog-Rock/Art Rock
    Proto-Punk
    Psychedelic
    Psychedelic Pop
    Psychedelic Soul
    Psychobilly
    Pub Rock
    Punk
    Punk Blues
    Punk Metal
    Punk-Pop
    Punk Revival
    Pygmy
    Qawwali
    Quadrille
    Quechua
    Queercore
    Quiet Storm
    Radio Plays
    Radio Shows
    Radio Works
    Raga
    Ragga
    Ragtime
    Rai
    Rakugo
    Ranchera
    Rap
    Rap-Metal
    Rap-Rock
    Rapso
    Rave
    R&B
    Read-Along Stories
    Reggae
    Reggae Gospel
    Reggae-Pop
    Reggaeton
    Relaxation
    Rembetika
    Renaissance
    Retro-Rock
    Retro-Soul
    Retro Swing
    Rhumba
    Riot Grrrl
    Rock
    Rockabilly
    Rockabilly Revival
    Rock en Espa√±ol
    Rock & Roll
    Rocksteady
    Rodeo
    Romantic
    Roots Reggae
    Roots Rock
    Russian Folk
    Sacred Choral
    Sacred Vocal
    Sadcore
    Salsa
    Samba
    Satire
    Scandinavian Folk
    Scandinavian Metal
    Scandinavian Pop
    Scottish Folk
    Screamo
    Scriptures
    Sea Shanties
    S√©ga
    Self-Help
    Shaabi
    Sharki
    Shibuya-Kei
    Shinto
    Shock Jock
    Shoegaze
    Show Tunes
    Sing-Along
    Singer/Songwriter
    Ska
    Ska-Punk
    Ska Revival
    Skatepunk
    Sketch Comedy
    Skiffle
    Slack-Key Guitar
    Slide Guitar Blues
    Slowcore
    Sludge Metal
    Smooth Jazz
    Smooth Reggae
    Smooth Soul
    Soca
    Social Commentary
    Society Dance Band
    Soft Rock
    Software/Sounds
    Solo Instrumental
    Son
    Sonero
    Song Parody
    Songster
    Sophisti-Pop
    Soukous
    Soul
    Soul-Blues
    Soul-Jazz
    Sound Art
    Sound Collage
    Sound Effects
    Sound Sculpture
    Soundtrack
    Soundtracks
    South African Folk
    South African Pop
    Southeast Asian Music
    Southern Gospel
    Southern Rap
    Southern Rock
    Southern Soul
    Space
    Space Age Pop
    Space Rock
    Spanish Folk
    Speeches
    Speed Metal
    Spiritual
    Spirituals
    Spoken Word
    Sports Anthems
    Spouge
    Spy Music
    Square Dance
    Standards
    Standup Comedy
    Steel Band
    St. Louis Blues
    Stoner Metal
    Straight-Edge
    Stride
    String Bands
    Structured Improvisation
    Sunshine Pop
    Surf
    Surf Revival
    Swamp Blues
    Swamp Pop
    Swedish Folk
    Swedish Pop/Rock
    Sweet Bands
    Swing
    Switzerland Folk
    Symphonic Black Metal
    Synth Pop
    Taarab
    Taiwanese Pop
    Tango
    Tape Music
    Tech-House
    Techno
    Techno Bass
    Techno-Dub
    Techno-Tribal
    Teen Idol
    Teen Pop
    Tejano
    Television Music
    Texas Blues
    Tex-Mex
    Thai Pop
    Thanksgiving
    Third Stream
    Third Wave Ska Revival
    Thrash
    Throat Singing
    Timba
    Tin Pan Alley Pop
    Torch Songs
    Township Jazz
    Traditional
    Traditional Bluegrass
    Traditional Cajun
    Traditional Celtic
    Traditional Chinese
    Traditional Country
    Traditional European Folk
    Traditional Folk
    Traditional Gospel
    Traditional Irish Folk
    Traditional Japanese
    Traditional Korean
    Traditional Middle Eastern Folk
    Traditional Native American
    Traditional Pop
    Traditional Scottish Folk
    Trad Jazz
    Trance
    Travel
    Tribal-House
    Tribute Albums
    Trip-Hop
    Tropical
    Tropicalia
    Trot
    Trova
    Truck Driving Country
    Turkish Folk
    Turkish Music
    Turntablism
    TV Soundtracks
    Twee Pop
    Underground Rap
    Uptown Soul
    Urban
    Urban Blues
    Urban Cowboy
    Urban Folk
    Vallenato
    Vaudeville
    Vaudeville Blues
    Vaudou
    Video
    Video Game Music
    Vocal
    Vocalese
    Vocal Jazz
    Vocal Pop
    Waltz
    Wedding Music
    West Coast Blues
    West Coast Jazz
    West Coast Rap
    Western Swing
    Western Swing Revival
    Women's
    Work Songs
    World
    Worldbeat
    World Fusion
    Yodeling
    Zouk
    Zydeco
*/


    public String getTextual()
    {
        return _textual;
    }
}
