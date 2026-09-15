package com.fabianoanticona.itanes.data.local.seed;

import android.util.Log;

import com.fabianoanticona.itanes.data.local.entity.PlaceEntity;
import com.fabianoanticona.itanes.data.repository.PlaceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaceDataSeeder {

    private static final String TAG = "PlaceDataSeeder";
    private final PlaceRepository repository;
    private final ExecutorService executorService;

    public PlaceDataSeeder(PlaceRepository repository) {
        this.repository = repository;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public interface SeedCallback {
        void onComplete();
    }

    public void seed() {
        seed(null);
    }

    public void seed(SeedCallback callback) {
        executorService.execute(() -> {
            int count = repository.getCount();
            if (count == 0) {
                List<PlaceEntity> initialPlaces = new ArrayList<>();

                initialPlaces.add(new PlaceEntity(
                        1,
                        "Oasis de la Huacachina",
                        "Un espectacular oasis natural rodeado de inmensas dunas de arena en medio del desierto.",
                        "La Laguna de Huacachina es un oasis de aguas color verde esmeralda surgido por el afloramiento de corrientes subterráneas. Está rodeado de una frondosa vegetación de palmeras, eucaliptos y huarangos. Es uno de los destinos más icónicos del país, famoso a nivel mundial tanto por su belleza paisajística como por los emocionantes paseos en carros tubulares y la práctica de sandboard en sus gigantescas dunas desérticas.",
                        "Balneario de Huacachina s/n, Ica, Perú",
                        -14.0875,
                        -75.7633,
                        "https://coviperu.com/wp-content/uploads/2019/12/1200x800.jpg",
                        1,
                        "2026-08-31"
                ));

                initialPlaces.add(new PlaceEntity(
                        2,
                        "Reserva Nacional de Paracas",
                        "Santuario marino que protege una asombrosa biodiversidad y un desierto costero único.",
                        "Ubicada en la provincia de Pisco dentro de la región de Ica, la Reserva Nacional de Paracas es una zona protegida que abarca tanto ecosistemas marinos como porciones de desierto costero. El lugar destaca por sus impresionantes acantilados, playas de arena rojiza y su gran riqueza biológica, sirviendo de hogar para lobos marinos, pingüinos de Humboldt, delfines y una enorme variedad de aves guaneras. Su cercanía a las Islas Ballestas la convierte en un paso obligatorio para el turismo de naturaleza.",
                        "Carretera Punta Pejerrey Km. 27, Paracas, Pisco, Ica, Perú",
                        -13.8694,
                        -76.2653,
                        "https://bestwayperu.com/wp-content/uploads/2020/11/reserva-nacional-de-paracas-ica.jpg",
                        2,
                        "2026-08-31"
                ));

                initialPlaces.add(new PlaceEntity(
                        3,
                        "Líneas de Nazca",
                        "Enigmáticos geoglifos precolombinos grabados en las áridas pampas de Nazca.",
                        "Las Líneas de Nazca son una serie de antiguos geoglifos trazados en el suelo del desierto por la cultura Nazca entre los siglos I a.C. y VII d.C. Estas monumentales figuras, que representan animales, plantas y diseños geométricos, solo pueden apreciarse en su total magnitud mediante un sobrevuelo en avioneta o desde miradores elevados. Declaradas Patrimonio de la Humanidad, continúan siendo uno de los mayores misterios arqueológicos del mundo debido a su precisión y longevidad.",
                        "Panamericana Sur Km. 419, Nazca, Ica, Perú",
                        -14.6923,
                        -75.1205,
                        "https://perutogethertravel.com/wp-content/uploads/2024/10/lineas-de-nazca-1.png",
                        3,
                        "2026-08-31"
                ));

                initialPlaces.add(new PlaceEntity(
                        4,
                        "Viñedo Tacama",
                        "El viñedo más antiguo de Sudamérica, ideal para conocer el origen del pisco y el vino.",
                        "Establecido en la década de 1540 por los españoles, el Viñedo Tacama ostenta el título de la bodega vitivinícola más antigua del continente sudamericano. Ubicado en el corazón del valle de Ica, este oasis agrícola utiliza tecnología de punta combinada con tradición colonial para elaborar piscos y vinos de alta calidad. Los visitantes pueden recorrer sus instalaciones históricas, observar los procesos de destilación modernos y disfrutar de catas guiadas rodeados de bellos paisajes andinos.",
                        "Camino Real s/n, La Tinguiña, Ica, Perú",
                        -14.0125,
                        -75.7264,
                        "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/05/c2/c7/ae/campanario.jpg?w=1200&h=1200&s=1",
                        4,
                        "2026-08-31"
                ));

                initialPlaces.add(new PlaceEntity(
                        5,
                        "Cañón de los Perdidos",
                        "Una imponente formación geológica esculpida por el agua y el viento en el desierto.",
                        "El Cañón de los Perdidos es una maravilla natural oculta en el sudoeste del distrito de Santiago, en pleno desierto iqueño. Con una profundidad de entre 100 y 300 metros, esta grieta rocosa fue erosionada a lo largo de millones de años por el paso del río Ica y los vientos costeros. El recorrido por su interior revela diferentes niveles, lagunas estacionales y caprichosas formas talladas en las paredes arcillosas, ideal para los amantes del trekking y la aventura extrema.",
                        "Desierto de Ocucaje - Sector Sudoeste, Santiago, Ica, Perú",
                        -14.3972,
                        -75.7831,
                        "https://perucheaptours.com/wp-content/uploads/2024/10/Canon-de-los-Perdidos.jpg",
                        5,
                        "2026-08-31"
                ));

                repository.insertPlaces(initialPlaces);
            }

            int finalCount = repository.getCount();
            Log.d(TAG, "PlaceDataSeeder: " + finalCount + " lugares disponibles en Room");

            if (callback != null) {
                callback.onComplete();
            }
        });
    }
}