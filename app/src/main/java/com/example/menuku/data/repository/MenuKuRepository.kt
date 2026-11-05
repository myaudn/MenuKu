package com.example.menuku.data.repository

import com.example.menuku.data.models.*
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MenuKuRepository {

    // Initial Recipes
    private val initialRecipes = listOf(
        Recipe(
            id = "soto-ayam",
            title = "Soto Ayam Lamongan Asli",
            subtitle = "Kuah kuning rempah kunyit harum dengan taburan koya udang renyah",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCzuNB4kexqxRqRAFXcsFJZvMZkYRN7yFJ7MUCoFWHvxSBIL4WshwRTA7dPFqiAUMmhW3s5tj7GQwPoydeB9mFPkNbf6-npvEBYtb53eUh3FRN-Z5XsE0U_Ky9VvJuXyEOJCzFTkjMueH0ASHqZNfE5On3G92PHfzZZZprS-TIc4MkcDEn5fit0XatIRvE9zcJoucylrfSqyWk_KZqOFhIEd9J2wTACpsjvdxB1ZNnX",
            rating = 4.9,
            reviewCount = 124,
            prepTimeMinutes = 45,
            portions = 4,

            difficulty = "Sedang",
            category = "Makan Siang",
            isAvailableInKitchen = true,
            missingIngredientsCount = 0,
            isFavorite = true,
            tags = listOf("Tradisional", "Favorit Keluarga", "Kuah Hangat"),
            chefNote = "Kunci kelezatan koya adalah kerupuk udang kualitas bagus yang digiling halus bersama bawang putih goreng renyah.",
            ingredients = listOf(
                IngredientItem("Dada Ayam Fillet", 500.0, "gram", true),
                IngredientItem("Bumbu Soto Kuning Halus", 1.0, "porsi", true),
                IngredientItem("Kerupuk Udang & Bawang (Koya)", 50.0, "gram", true),
                IngredientItem("Soun & Kol Segar", 100.0, "gram", true),
                IngredientItem("Jeruk Nipis & Daun Seledri", 2.0, "buah", true),
                IngredientItem("Telur Rebus", 4.0, "butir", true)
            ),
            steps = listOf(
                CookingStepItem(1, "Rebus dada ayam fillet bersama serai, daun jeruk, dan jahe hingga matang kaldu harum.", 900, "Gunakan api kecil agar kaldu jernih."),
                CookingStepItem(2, "Tumis bumbu halus kuning dengan sedikit minyak hingga matang harum, masukkan ke kuah rebusan kaldu.", 600, "Pastikan bumbu tanak agar tidak langu."),
                CookingStepItem(3, "Angkat dada ayam, tiriskan lalu suwir-suwir memanjang.", 300, ""),
                CookingStepItem(4, "Giling kerupuk udang goreng dan bawang putih goreng kering untuk taburan bubuk koya.", 300, "Jangan terlalu lama dibiarkan terbuka agar tetap renyah."),
                CookingStepItem(5, "Susun soun, kol, ayam suwir, dan siram kuah panas. Beri taburan koya melimpah dan air perasan jeruk nipis.", 180, "Sajikan hangat.")
            )
        ),
        Recipe(
            id = "tumis-buncis",
            title = "Tumis Buncis Daging Cincang",
            subtitle = "Renyah, gurih, dan cepat disajikan untuk santap malam sehat",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBCFk_W-05x5GNfMyTvPS6h5KiQ64l08N-RIdIRm0tw4iQOaaeCCDOqYTYpLHeHXx-SbbaZZzr38-B8jXPQswjjRgN1jNQN01xKgoiJpKvCDN5YcP3jAlyYRj0GzQU8jF4itcB9gpj3qEduKTRkcyEn9TixUXsHBLvMjqPF_PlKDEdok3aL6TPmNEVe99ZhCP8PIdL5dCK9kN3v73FNxYWxD3s_BYBMdEwM8W81-dNj",
            rating = 4.8,
            reviewCount = 89,
            prepTimeMinutes = 20,
            portions = 3,

            difficulty = "Mudah",
            category = "Makan Malam",
            isAvailableInKitchen = true,
            missingIngredientsCount = 0,
            isFavorite = true,
            tags = listOf("Tinggi Serat", "Praktis Cepat", "Diet Sehat"),
            chefNote = "Gunakan api besar saat menumis buncis agar warna hijaunya tetap cerah dan teksturnya 'crunchy'.",
            ingredients = listOf(
                IngredientItem("Buncis Baby Segar", 250.0, "gram", true),
                IngredientItem("Daging Sapi Cincang Tanpa Lemak", 150.0, "gram", true),
                IngredientItem("Bawang Putih Cincang", 3.0, "siung", true),
                IngredientItem("Cabai Merah Keriting", 2.0, "buah", true),
                IngredientItem("Saus Tiram Rendah Natrium", 1.5, "sdm", true)
            ),
            steps = listOf(
                CookingStepItem(1, "Panaskan 1 sendok teh minyak di wajan, tumis bawang putih hingga harum kecokelatan.", 120),
                CookingStepItem(2, "Masukkan daging sapi cincang, aduk cepat hingga berubah warna dan matang merata.", 240),
                CookingStepItem(3, "Masukkan buncis yang telah dipotong, bumbui dengan saus tiram dan lada hitam.", 180),
                CookingStepItem(4, "Aduk di atas api besar selama 2 menit, angkat dan sajikan segera selagi hangat.", 120)
            )
        ),
        Recipe(
            id = "rendang-sapi",
            title = "Rendang Sapi Daging Empuk",
            subtitle = "Serat daging lembut berselimut bumbu karamel rempah autentik Minang",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBEc-k64u5wMYTEk_UJR3lqeGz_RlkFXUerNS6CMaP3yfXMtv5fz2BgSOtFj9BTJ1fM-4iKQ55Cn1rSwQ1xlkpNjd_fOfJf_PkHOQ7Y0Imc69SfDA8UDr9yfDMfYAHWnMnSlNKKkhz8Fd6KtJrrVelUiJCLrTRMIMqn3T0wYV9g26dXiq5SXP404n-ERQbm3hGEdCO2jjtuH_klFo9ohqy7HqGapkvpBLd5_chpOjQt",
            rating = 5.0,
            reviewCount = 210,
            prepTimeMinutes = 120,
            portions = 6,

            difficulty = "Ahli",
            category = "Tradisional Nusantara",
            isAvailableInKitchen = false,
            missingIngredientsCount = 2,
            isFavorite = false,
            tags = listOf("Tradisional", "Spesial Akhir Pekan", "Kaya Rempah"),
            chefNote = "Gunakan santan kelapa tua murni dari perasan pertama agar minyak alami kelapa keluar maksimal.",
            ingredients = listOf(
                IngredientItem("Daging Sapi Gandik / Paha", 800.0, "gram", true),
                IngredientItem("Santan Kelapa Kental Murni", 1000.0, "ml", false),
                IngredientItem("Bumbu Rempah Rendang Minang", 1.0, "porsi", true),
                IngredientItem("Kelapa Parut Sangrai (Ambual)", 100.0, "gram", false),
                IngredientItem("Daun Kunyit & Asam Kandis", 2.0, "lembar", true)
            ),
            steps = listOf(
                CookingStepItem(1, "Didihkan santan bersama bumbu halus, daun kunyit, dan serai sambil ditimba-timba perlahan.", 1800),
                CookingStepItem(2, "Setelah santan mengeluarkan minyak kemerahan (kalio), masukkan potongan daging sapi.", 2400),
                CookingStepItem(3, "Kecilkan api, aduk perlahan sampai kuah mengering menjadi rendang cokelat kehitaman harum.", 3600)
            )
        ),
        Recipe(
            id = "ayam-panggang-herbal",
            title = "Dada Ayam Panggang Herbal",
            subtitle = "Marinasi rempah Mediterania dengan minyak zaitun murni dan perasan lemon",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBxBM-6WkKNdXC9Z6JCXr3iC3UaQxuLt1m9y1QshqlQWynz4FbKfEgsouE2JSlfZmFzekBbmgLB14Nwdff3qGuVtKbJfNsccFTT71LZkXpbp1mtvwEVR_rw58N3xHexa3LSiuLQfMCvx_bgCvB-7xUUXxSa64YbtTz3KRp7cyEnx23O3kign08z7bR01um3_hcrx-humQM9NCvC2ASWA0DTLw-t9NIOMUBDhLJBVC_3",
            rating = 4.9,
            reviewCount = 124,
            prepTimeMinutes = 25,
            portions = 2,

            difficulty = "Mudah",
            category = "Diet Sehat",
            isAvailableInKitchen = true,
            missingIngredientsCount = 0,
            isFavorite = true,
            tags = listOf("Tinggi Protein", "Diet Sehat", "Rendah Kalori"),
            chefNote = "Marinasi minimal 2 jam di kulkas agar bumbu meresap ke serat terdalam dada ayam.",
            ingredients = listOf(
                IngredientItem("Dada Ayam Fillet", 300.0, "gram", true),
                IngredientItem("Rosemary Segar & Oregano", 1.0, "sdm", true),
                IngredientItem("Minyak Zaitun Extra Virgin", 1.0, "sdm", true),
                IngredientItem("Bawang Putih Halus", 2.0, "siung", true),
                IngredientItem("Lemon Segar", 1.0, "buah", true)
            ),
            steps = listOf(
                CookingStepItem(1, "Sayat ringan permukaan dada ayam, lumuri rosemary, bawang putih, olive oil, dan garam.", 300),
                CookingStepItem(2, "Panaskan grill pan, panggang dada ayam selama 6 menit per sisi hingga keemasan juicy.", 720),
                CookingStepItem(3, "Beri perasan lemon segar sebelum disajikan bersama sayuran panggang.", 120)
            )
        ),
        Recipe(
            id = "pancake-pisang",
            title = "Pancake Pisang Oatmeal",
            subtitle = "Manis alami tanpa gula pasir, sarapan tinggi serat untuk energi pagi",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCerJUnh4XFXHYuZO8yRsvU0WZjGs1KHvm2dPuMgXk1puZ1soL-0i8fO4SxlMLkA3R6FwJLHy5pysqEFXCpiYA-E7wf_vgAMe31KqP9WL8-pzpGejTpYHObim6iE-Ub4RSwFg0V9VYaY1ElLELbTfEUBvO0Q9kgbe8QYr_wI_sYoqJ6_OoLdmpc5e5pPlV8G4RPH82U4Uecv_wpaCADLeyKONH4qbsjxBHT_2G5S6xl",
            rating = 4.7,
            reviewCount = 63,
            prepTimeMinutes = 15,
            portions = 2,

            difficulty = "Sangat Mudah",
            category = "Sarapan",
            isAvailableInKitchen = true,
            missingIngredientsCount = 0,
            isFavorite = true,
            tags = listOf("Sarapan", "Bebas Gula", "Anak & Keluarga"),
            chefNote = "Gunakan pisang raja atau pisang cavendish yang sangat matang untuk rasa manis karamel alami.",
            ingredients = listOf(
                IngredientItem("Pisang Matang", 2.0, "buah", true),
                IngredientItem("Rolled Oat Halus", 80.0, "gram", true),
                IngredientItem("Telur Ayam", 1.0, "butir", true),
                IngredientItem("Madu Murni & Buah Beri", 2.0, "sdm", true)
            ),
            steps = listOf(
                CookingStepItem(1, "Haluskan pisang dengan garpu di mangkuk, campurkan telur dan oatmeal hingga adonan kental merata.", 180),
                CookingStepItem(2, "Panggang sesendok adonan di teflon anti lengket tanpa minyak hingga bersarang, lalu balik.", 300),
                CookingStepItem(3, "Sajikan tumpuk hangat dengan tetesan madu hutan dan potongan buah segar.", 120)
            )
        ),
        Recipe(
            id = "sup-salmon",
            title = "Sup Ikan Salmon Kuah Bening",
            subtitle = "Segar gurih kaldu jahe aromatik tanpa bau amis, kaya asam lemak omega-3",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBhO_ORAf5tkUcJ7sUIlCmX6GamiBv5Q2U7GeWbTVjKnprpGPnQhsbrpzZ_SMW_5yhLXdJ0vCsufv_YyIUCd39xFcWQCDuCcPMNuYdgEZRaS0noHgyoZEjZGL3sG2OoyZwADs-sgrcZ3_Cyf4wzxOgwQ3aZYuXTDef6wXXwViRdoz5JXjx-11dRYVSJ18kYqzGNy7UFzg-bLoPpNuXGwTLO6GY8pQlOzHtsLfyv8jF9",
            rating = 5.0,
            reviewCount = 210,
            prepTimeMinutes = 30,
            portions = 3,

            difficulty = "Sedang",
            category = "Makan Siang",
            isAvailableInKitchen = true,
            missingIngredientsCount = 0,
            isFavorite = true,
            tags = listOf("Omega-3", "Diet Sehat", "Kuah Bening"),
            chefNote = "Masukkan salmon di menit-menit akhir memasak agar daging ikan tetap empuk dan tidak hancur.",
            ingredients = listOf(
                IngredientItem("Potongan Fillet Salmon", 300.0, "gram", true),
                IngredientItem("Wortel & Brokoli", 100.0, "gram", true),
                IngredientItem("Jahe Memar & Serai", 1.0, "batang", true),
                IngredientItem("Daun Kemangi & Tomat Ceri", 1.0, "ikat", true)
            ),
            steps = listOf(
                CookingStepItem(1, "Didihkan air bersama jahe memar, serai, daun salam hingga harum.", 600),
                CookingStepItem(2, "Masukkan wortel dan brokoli, masak sebentar hingga setengah matang.", 300),
                CookingStepItem(3, "Masukkan salmon, bumbui garam himalaya dan lada. Masak 3-4 menit.", 240),
                CookingStepItem(4, "Matikan api, masukkan daun kemangi dan perasan jeruk nipis.", 60)
            )
        ),
        Recipe(
            id = "nasi-uduk",
            title = "Nasi Uduk Betawi Komplit",
            subtitle = "Gurih harum serai, daun salam dengan taburan orek tempe dan telur iris",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAje96QmbWFofqwbrpPkb7xJ6oKJNVZwKKuL5kbCIxdrSQ3hD32vG3hLXU0hqvcqwetsNzlQ1FH5Cqva_TbR4l_gcWllNeeiMKrexq4sO5Q2Y68hw9y10vl-7J_O9PibFU9MVd_uH1DGoidJx7DfVHfw1XMkfYj-jN65BuYGqWh0Uzj9sqKe4MzUAAq4U8DQY3h6ztwWWv1azwlRl91kts-IHBv3ct23fckf8qimMyv",
            rating = 4.8,
            reviewCount = 98,
            prepTimeMinutes = 35,
            portions = 4,

            difficulty = "Mudah",
            category = "Sarapan",
            isAvailableInKitchen = true,
            missingIngredientsCount = 0,
            isFavorite = false,
            tags = listOf("Sarapan", "Tradisional", "Komplit"),
            chefNote = "Kukus nasi setelah diaron dengan santan agar butiran nasi pulen dan wangi semerbak.",
            ingredients = listOf(
                IngredientItem("Beras Putih Pulen", 400.0, "gram", true),
                IngredientItem("Santan Kelapa", 500.0, "ml", true),
                IngredientItem("Daun Pandan, Salam & Serai", 1.0, "ikat", true),
                IngredientItem("Tempe Orek Manis", 100.0, "gram", true),
                IngredientItem("Telur Dadar Rawis", 2.0, "butir", true)
            ),
            steps = listOf(
                CookingStepItem(1, "Cuci beras, aron bersama santan, daun pandan, serai, daun salam dan sedikit garam hingga air terserap.", 600),
                CookingStepItem(2, "Pindahkan ke kukusan panas, kukus hingga matang tanak selama 25 menit.", 1500),
                CookingStepItem(3, "Sajikan hangat beralas daun pisang dengan tempe orek, telur iris, dan sambal terasi.", 180)
            )
        ),
        Recipe(
            id = "sayur-asem",
            title = "Sayur Asem Khas Betawi",
            subtitle = "Kuah asam segar aroma terasi asam jawa dengan melinjo dan jagung manis",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB0B4L8gysPmQ1n5At_fj6E6mDZPkwQoMwxxeeQfovLdXzJAYgZFpASqhXadHckUC0DUUBA-xEAYUhOP6MwKv02Pmh-flY2eDm1AnRTGEKNWmeF7vFD3i5a_KfsC_7VT3dJBFmg_C_af6t4YgsceDgiO0hPASe7gGw7X0zN3ReG-f6WOGJiTz5ljXyJo7fvWRfE8gUDuHi4bPmSgbXqD6dSdgD1x06c4GE_fkn2WQX5",
            rating = 4.8,
            reviewCount = 76,
            prepTimeMinutes = 25,
            portions = 4,

            difficulty = "Mudah",
            category = "Makan Siang",
            isAvailableInKitchen = true,
            missingIngredientsCount = 0,
            isFavorite = false,
            tags = listOf("Sayuran Segar", "Tradisional", "Rendah Kalori"),
            chefNote = "Gunakan asam jawa segar yang dilarutkan dengan air hangat untuk aroma asam yang bersih alami.",
            ingredients = listOf(
                IngredientItem("Jagung Manis Potong", 1.0, "buah", true),
                IngredientItem("Labu Siam & Kacang Panjang", 150.0, "gram", true),
                IngredientItem("Biji Melinjo & Daun So", 50.0, "gram", true),
                IngredientItem("Kacang Tanah Kupas", 50.0, "gram", true),
                IngredientItem("Air Asam Jawa & Bumbu Halus", 1.0, "porsi", true)
            ),
            steps = listOf(
                CookingStepItem(1, "Didihkan air, masukkan bumbu halus, daun salam, lengkuas, kacang tanah, dan jagung.", 600),
                CookingStepItem(2, "Masukkan labu siam dan melinjo, masak sampai empuk.", 300),
                CookingStepItem(3, "Beri air asam jawa, garam dan gula merah. Masukkan kacang panjang dan daun melinjo sesaat sebelum diangkat.", 180)
            )
        ),
        Recipe(
            id = "ayam-bakar-madu",
            title = "Ayam Bakar Madu Pedas Manis",
            subtitle = "Kilauan saus karamel madu pedas manis dengan aroma panggangan arang",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDY8o4e3KCq4Ng-97llsHYjyroTvo7acDeHOzw6pGq7Uizi6ItsPi6zOBYRl8gqzJSe9YdfXxOHxNZtNi3kH_fEBoDeUisGn64sgk4ltdCHJfP47Y5bS8_EcicH3xi3LI_OQaoLN-RYaHTPnKwITYxR5kRaHZPFm0o1NgT1pAARLg1bWxrnOqiYmNN8PMdPa-pOcGUCIjuRP9xk22__zWqckrvHH7ENZSPAOBbqIAbM",
            rating = 4.9,
            reviewCount = 145,
            prepTimeMinutes = 45,
            portions = 4,

            difficulty = "Sedang",
            category = "Makan Malam",
            isAvailableInKitchen = true,
            missingIngredientsCount = 0,
            isFavorite = false,
            tags = listOf("Lauk Spesial", "Panggangan", "Pedas Manis"),
            chefNote = "Ungkep ayam hingga empuk dan bumbu meresap sebelum dibakar sambil terus diolesi madu.",
            ingredients = listOf(
                IngredientItem("Potongan Daging Ayam", 600.0, "gram", true),
                IngredientItem("Madu Murni", 3.0, "sdm", true),
                IngredientItem("Kecap Manis & Saus Cabai", 2.0, "sdm", true),
                IngredientItem("Bumbu Ungkep Rempah", 1.0, "porsi", true)
            ),
            steps = listOf(
                CookingStepItem(1, "Ungkep ayam bersama bumbu halus hingga bumbu meresap dan kuah menyusut.", 1200),
                CookingStepItem(2, "Campur madu, mentega, dan kecap manis untuk saus olesan panggangan.", 180),
                CookingStepItem(3, "Bakar ayam di atas panggangan sambil diolesi saus madu bolak-balik hingga harum kecokelatan.", 600)
            )
        )
    )

    // Initial Collections
    private val initialCollections = listOf(
        RecipeCollection(
            id = "diet-sehat",
            title = "Menu Diet Sehat",
            description = "Koleksi khusus hidangan rendah kalori, minim minyak goreng, dan kaya protein untuk program defisit kalori keluarga tanpa mengurangi kelezatan rasa.",
            coverUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC8hVMk-NZOJCN-5oy0Fxbr9LdAGB5XEcqo5-PuEkbIjJ3cdKfGabTPNVqiDz2pPfscfxpvjV671BVtFa7ju98qkkT0atDR-lN0mAOD5tNZltBtLoeIVFpD4KirzDkEdqBaktlZc2NBXEUtWJb6PTZ0_u4FxfSq_7yrlLeDSJsw2SpFFy-DqzSO9ty8qQjnwuBtAl24Hu-aYRDEfizjLEvS2JPzhY609kePO1nw7f9f",
            recipeCount = 6,

            difficulty = "Semua Mudah",
            authorNote = "Selalu ganti garam biasa dengan garam himalaya, dan gunakan olive oil maksimal 1 sendok makan per porsi.",
            iconName = "eco",
            themeColor = "hijau",
            recipeIds = listOf("ayam-panggang-herbal", "tumis-buncis", "sup-salmon", "pancake-pisang")
        ),
        RecipeCollection(
            id = "kilat-20",
            title = "Masakan Kilat <20 Menit",
            description = "Solusi praktis saat waktu memasak terbatas di sela-sela rutinitas kerja dan keluarga.",
            coverUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC34SHC4rQKDBUqphiFFyzw99NFt1HxGwtrQD5sjPqEkbvVTR0iT1jSiCNRSMVYs1qc_K55rRv24XjSNBHjW6YFXdM0TSQclVf3agi5Zag2wWWwSBDiNs15acm162_qiHzZTwo7fMJCBtITJbkE1ECLXrh5afVNBiu--IfV1S3DCDKp3tJjwEiSFdbb9irj9vpH4_uND3TXUdJ5TZlU6IaMa89JKPCNrANyAo8lBt66",
            recipeCount = 8,

            difficulty = "Sangat Cepat",
            authorNote = "Siapkan bumbu dasar putih dan merah di kulkas setiap awal pekan agar waktu masak makin singkat.",
            iconName = "bolt",
            themeColor = "oranye",
            recipeIds = listOf("tumis-buncis", "pancake-pisang")
        ),
        RecipeCollection(
            id = "favorit-si-kecil",
            title = "Favorit Si Kecil",
            description = "Menu ramah anak, kaya nutrisi pembangun tumbuh kembang dan tanpa rasa pedas menyengat.",
            coverUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCerJUnh4XFXHYuZO8yRsvU0WZjGs1KHvm2dPuMgXk1puZ1soL-0i8fO4SxlMLkA3R6FwJLHy5pysqEFXCpiYA-E7wf_vgAMe31KqP9WL8-pzpGejTpYHObim6iE-Ub4RSwFg0V9VYaY1ElLELbTfEUBvO0Q9kgbe8QYr_wI_sYoqJ6_OoLdmpc5e5pPlV8G4RPH82U4Uecv_wpaCADLeyKONH4qbsjxBHT_2G5S6xl",
            recipeCount = 5,

            difficulty = "Mudah",
            authorNote = "Pancake pisang oat dan nugget ayam buatan sendiri selalu jadi andalan sarapan anak-anak.",
            iconName = "child_care",
            themeColor = "kuning",
            recipeIds = listOf("pancake-pisang", "soto-ayam")
        )
    )

    // Initial Pantry Stock Items
    private val initialStockItems = listOf(
        StockItem(
            id = "stk-1",
            name = "Dada Ayam Fillet",
            quantity = 500.0,
            unit = "gram",
            location = StorageLocation.KULKAS,
            category = "Daging & Telur",
            purchaseDate = "19 Nov 2024",
            expiryDate = "23 Nov 2024",
            remainingDays = 2,
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDn9XxdtwiUFgIt_0WnqBiK8s-0PLBH4Irp9QWNaWw3EOSR1fKr2dAiT3JQ0KTRJCKu9hDGi7Cot7-cwPkLHHLtnkhQQQ47xjnuTDT340aVz88KHBaT1NcRz-i0tKbWu921hvXTdVSZ7b8CICgRS_SYP_-GPZdNgeVIalpD97FJ8cqPG868hm05SF7ReulMjB8HouEwdxRpJ9xZam-nMFn5w21ZpqHiQdSauNUmStn4",
            note = "Segar tanpa kulit, simpan di chiller 3°C dalam wadah kedap udara.",
            matchedRecipes = listOf("Soto Ayam Lamongan Asli", "Dada Ayam Panggang Herbal")
        ),
        StockItem(
            id = "stk-2",
            name = "Susu UHT Full Cream",
            quantity = 1.0,
            unit = "liter",
            location = StorageLocation.KULKAS,
            category = "Susu & Olahan",
            purchaseDate = "15 Nov 2024",
            expiryDate = "24 Nov 2024",
            remainingDays = 2,
            imageUrl = "",
            note = "Sudah dibuka 2 hari lalu, segera habiskan untuk sarapan atau olahan sup.",
            matchedRecipes = listOf("Pancake Pisang Oatmeal")
        ),
        StockItem(
            id = "stk-3",
            name = "Bawang Merah Brebes",
            quantity = 350.0,
            unit = "gram",
            location = StorageLocation.PANTRI,
            category = "Bumbu Rempah",
            purchaseDate = "18 Nov 2024",
            expiryDate = "05 Des 2024",
            remainingDays = 14,
            note = "Simpan di rak terbuka berventilasi baik agar tidak lembap berjamur.",
            matchedRecipes = listOf("Soto Ayam Lamongan Asli", "Nasi Uduk Betawi Komplit")
        ),
        StockItem(
            id = "stk-4",
            name = "Telur Ayam Negeri",
            quantity = 8.0,
            unit = "butir",
            location = StorageLocation.KULKAS,
            category = "Daging & Telur",
            purchaseDate = "17 Nov 2024",
            expiryDate = "02 Des 2024",
            remainingDays = 11,
            note = "Kondisi bersih tanpa retak, letakkan di egg tray bagian tengah.",
            matchedRecipes = listOf("Soto Ayam Lamongan Asli", "Pancake Pisang Oatmeal")
        ),
        StockItem(
            id = "stk-5",
            name = "Brokoli Hijau Segar",
            quantity = 250.0,
            unit = "gram",
            location = StorageLocation.KULKAS,
            category = "Sayur & Buah",
            purchaseDate = "20 Nov 2024",
            expiryDate = "25 Nov 2024",
            remainingDays = 4,
            note = "Bungkus tisu kering sebelum dimasukkan ke kantong berpori.",
            matchedRecipes = listOf("Sup Ikan Salmon Kuah Bening")
        ),
        StockItem(
            id = "stk-6",
            name = "Santan Kelapa Instan",
            quantity = 3.0,
            unit = "bungkus",
            location = StorageLocation.PANTRI,
            category = "Sembako",
            purchaseDate = "10 Nov 2024",
            expiryDate = "15 Jan 2025",
            remainingDays = 55,
            note = "Kemasan tetra pak 65ml, stok kering aman.",
            matchedRecipes = listOf("Nasi Uduk Betawi Komplit", "Rendang Sapi Daging Empuk")
        )
    )

    // Initial Meal Schedules
    private val initialSchedules = listOf(
        MealSchedule(
            id = "sch-1",
            dateStr = "2024-11-21",
            dayName = "Kamis",
            dayNumber = 21,
            slot = MealSlot.SARAPAN,
            recipe = initialRecipes.find { it.id == "nasi-uduk" },
            isReadyInFridge = true,
            reminderTime = "06:15 WIB",
            reminderEnabled = true,
            note = "Siapkan bumbu serai dan orek tempe dari malam hari."
        ),
        MealSchedule(
            id = "sch-2",
            dateStr = "2024-11-21",
            dayName = "Kamis",
            dayNumber = 21,
            slot = MealSlot.SIANG,
            recipe = initialRecipes.find { it.id == "soto-ayam" },
            isReadyInFridge = true,
            reminderTime = "11:15 WIB",
            reminderEnabled = true,
            note = "Masak porsi lebih untuk makan malam jika ada sisa, koya digoreng pagi hari bersama sarapan."
        ),
        MealSchedule(
            id = "sch-3",
            dateStr = "2024-11-21",
            dayName = "Kamis",
            dayNumber = 21,
            slot = MealSlot.CAMILAN,
            recipe = null, // empty state
            isReadyInFridge = false,
            reminderTime = "15:30 WIB",
            reminderEnabled = false,
            note = ""
        ),
        MealSchedule(
            id = "sch-4",
            dateStr = "2024-11-21",
            dayName = "Kamis",
            dayNumber = 21,
            slot = MealSlot.MALAM,
            recipe = initialRecipes.find { it.id == "tumis-buncis" },
            isReadyInFridge = true,
            reminderTime = "18:15 WIB",
            reminderEnabled = true,
            note = "Tumis cepat 5 menit sebelum santap malam agar buncis tetap renyah."
        )
    )

    // Initial Shopping Items
    private val initialShoppingItems = listOf(
        ShoppingItem(
            id = "shp-1",
            name = "Daging Dada Ayam Segar",
            quantity = 500.0,
            unit = "gram",
            category = "protein",
            urgency = UrgencyLevel.URGENT,
            linkedRecipeTitle = "Soto Ayam Lamongan Asli",
            estimatedPrice = 38000L,
            storeRecommendation = "Pasar Modern - Kios Unggas Pak Budi",
            notes = "Beli yang tanpa kulit, minta dipotong jadi 4 bagian untuk rebusan kaldu soto siang ini. Pastikan daging segar tidak berair.",
            isPurchased = false,
            addedTime = "08:30"
        ),
        ShoppingItem(
            id = "shp-2",
            name = "Santan Kelapa Murni",
            quantity = 1000.0,
            unit = "ml",
            category = "sembako",
            urgency = UrgencyLevel.NORMAL,
            linkedRecipeTitle = "Rendang Sapi Daging Empuk",
            estimatedPrice = 24000L,
            storeRecommendation = "Tukang Kelapa Parut Pasar Pagi",
            notes = "Perasan pertama tanpa tambahan air berlebih.",
            isPurchased = false,
            addedTime = "09:00"
        ),
        ShoppingItem(
            id = "shp-3",
            name = "Kelapa Parut Sangrai (Ambual)",
            quantity = 100.0,
            unit = "gram",
            category = "bumbu",
            urgency = UrgencyLevel.NORMAL,
            linkedRecipeTitle = "Rendang Sapi Daging Empuk",
            estimatedPrice = 8000L,
            storeRecommendation = "Toko Bumbu Padang",
            notes = "Giling halus sampai berminyak harum.",
            isPurchased = false,
            addedTime = "09:15"
        ),
        ShoppingItem(
            id = "shp-4",
            name = "Cabai Rawit Merah",
            quantity = 150.0,
            unit = "gram",
            category = "sayur",
            urgency = UrgencyLevel.LOW,
            linkedRecipeTitle = "Sambal Soto Lamongan",
            estimatedPrice = 12000L,
            storeRecommendation = "Kios Sayur Segar",
            notes = "Pilih yang segar dan tidak lembek.",
            isPurchased = true,
            addedTime = "08:15"
        )
    )

    // Initial Taste Stories
    private val initialTasteStories = listOf(
        TasteStory(
            id = "tst-1",
            title = "Soto Ayam Lamongan Istimewa",
            recipeTitle = "Soto Ayam Lamongan Asli",
            dateFormatted = "Kamis, 21 November 2024",
            timeFormatted = "12:45 WIB",
            rating = 5.0f,
            photoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAP6JRPV77ImErcU7jv6zp7_51NfUAAleXKopK4BAEqofNAp5kNqWkvfJzvKIXDwdVN3V15XYLX9IcfwwMftSX2KmmfjigQ6LbRz6E_1N6RQGCk34Br21WSDMZSUjpVGJ-BiOlljqkiZWd49J15LgbSzG9l_3q_scpNuyDGqxkiYIfjscARLus1c1D8EOAjice6lfgLPR1m3Ssn5MHK6cGVm8OK9N8EtOlE5T-kqOYC",
            notes = "Eksperimen pertama bikin koya sendiri dari kerupuk udang dan bawang putih goreng! Kuahnya medok dan segar dengan perasan jeruk nipis ekstra. Suami dan anak-anak nambah 2 kali!",
            tags = listOf("KuahMedok", "KoyaGurih", "SegarJerukNipis", "ResepSukses"),
            cookTimeMinutes = 45,
            portionCount = 4,
            isFavorite = true,
            processThumbnails = listOf(
                "https://lh3.googleusercontent.com/aida-public/AB6AXuC2VcGDeMOT3xCwpqHr1aRaNZR3e5FL4drXTMslXJ4ZMWMvPV3wfGLUvOW71bE2gBtPSh-hhCd8lKnfFY0Nqi-EizF2SukBUxIQKk7uh05d2nyvgL40k34pyS3rD2OKao_e513j3amccK9xrh6pPB2KWAPH_I8BKqDDhuBoFUlrUyXv9ozVEyRcmczONJsOwfREbqWXap5Ubc64QIT9qHnFKETXvF_wapOQeTpO5Syf",
                "https://lh3.googleusercontent.com/aida-public/AB6AXuCxMY573r0_0mpUC7t6JNRUy_riFAXNyzRBNk1fB9S3OBBsV5rqwqStui6y4Txo4Y1t9oHZObPYChG_oRrHXR1u-I8g-WCMhZgjaDbQjzAcqUPVP5JODtIH3NF1O_nqJLDs26B_FJmofz5gH7jl1dGhI7t5HXewQCmWByH7msF_DNSkpb9cV5cizA5NfIXtNEIYrV58x-Mw94DqLX9gJRv-9bYxIWA2ZYDPQhX2vzci",
                "https://lh3.googleusercontent.com/aida-public/AB6AXuBqFr5Yj0Q1WDf24TCFBuPzG2aL91pQ5UqbtmsGPYvmPRzfe9E7ULSMgY235G0rlQuEevyMPtGNla2xceUt63X0OGNc-VChizNs4Uy_dd7L8DYQxk3JIPLBuoduHXQkr_Bc4QMLe7yRKmesfrBlls3DEsNRyrFU5RsBX2-EKBOt3Hz8YDzrGTgAlXim6JBWXOw7CQKAjXjre-sDqq3TM-jW3Lm-JJ_F3KaGmVUk2i5V"
            )
        ),
        TasteStory(
            id = "tst-2",
            title = "Ayam Panggang Herbal Juicy",
            recipeTitle = "Dada Ayam Panggang Herbal",
            dateFormatted = "Minggu, 17 November 2024",
            timeFormatted = "19:00 WIB",
            rating = 4.8f,
            photoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCMudBt32QGRSFnzdiK-Rg2a1tFkvgwcJAQUB8rneo-8qgJPnCJ01KhmZd46IqDqU_JJQL9gBUB0GF3qtrav7yagDh4vfZBHOHj6sKUOCF6hitD7ACOUmr7sKY2Ob8coLEln3RbL1d4oE_-Z00z8jtX4DyK3Tus2l9k4CP0ocAiQovYR3DrGwTSvevaG79-5IWiqvm-Zvtojlvc8vxJXC6Dc-gfRGskrfTR1veYSkvh",
            notes = "Marinasi rosemary segar dan lemon membuat dada ayam tetap empuk berair, tidak seret sama sekali.",
            tags = listOf("TinggiProtein", "DietEnak", "CleanEating"),
            cookTimeMinutes = 25,
            portionCount = 2,
            isFavorite = true
        ),
        TasteStory(
            id = "tst-3",
            title = "Nasi Uduk Wangi Pandan",
            recipeTitle = "Nasi Uduk Betawi Komplit",
            dateFormatted = "Senin, 18 November 2024",
            timeFormatted = "07:30 WIB",
            rating = 4.9f,
            photoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDLYRVcl1sx_5ug1KG9no2rZJAwC2c9ZYsJ_CW-_vgDzAmog7YCM-I6cnIPCqUWEVqNOju0FWO8mZ4zfoGTbKAzctSbAIH9ehCjVnwnZerdjjqJz3T4qcvmYuVIZl9PmYza8_bXUsYJZqOZZJa9X_qbDPd03P8r-G67Gh-94rpcwaJ1v4Jv5IBtqwYXCXxkEf1b3wX2aXHpO_J2IgzdB75Unof7ZHpC2vpEniKUrs-4",
            notes = "Aroma serai dan daun salam semerbak di dapur sejak jam 6 pagi. Cocok sekali disandingkan tempe orek garing.",
            tags = listOf("SarapanTradisional", "PagiCeria", "Pulen"),
            cookTimeMinutes = 35,
            portionCount = 4,
            isFavorite = false
        )
    )

    // Initial Notifications
    private val initialNotifications = listOf(
        AppNotification(
            id = "notif-1",
            title = "Waktunya Persiapan Soto Lamongan Asli",
            message = "Target santap siang keluarga pukul 12:30 WIB (45 mnt lagi). Bahan di kulkas lengkap 6/6 siap diolah!",
            timeLabel = "11:30 WIB",
            category = NotificationCategory.COOKING,
            isRead = false,
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCkIuMsVcmHt38NZIild8EDB7JjV32DpOjY791yAunrN390Ucjf-wWb0YwmFgfCVt7s7CN8qjAUhKs7Tp8RNkIteckvDI-MOlsb1uJUMkHzzfhHxQ_Ypxn1-QjcaYkoZe-nokFK1vTjq1H6YNG5svYcVYTIVw1CSUyspehYBiYjj96CGhZ-Fm5g1o9v50Fsg3McUpxbV6wZpkE6FPPGcF_yABbkKf0LNE5qAp_FxmGu",
            actionLabel = "Mulai Masak Sekarang"
        ),
        AppNotification(
            id = "notif-2",
            title = "Dada Ayam Fillet & Susu UHT Mendekati Kedaluwarsa",
            message = "2 bahan dapur tersisa 1-2 hari sebelum batas konsumsi. Segera olah untuk mencegah makanan terbuang sia-sia!",
            timeLabel = "09:15 WIB",
            category = NotificationCategory.PANTRY,
            isRead = false,
            actionLabel = "Olah Sekarang"
        ),
        AppNotification(
            id = "notif-3",
            title = "5 Bahan Belanja Mingguan Berhasil Ditambahkan",
            message = "Bawang merah, lengkuas, serai, daun jeruk, dan jeruk nipis sudah tercatat otomatis ke inventaris bumbu segar.",
            timeLabel = "Kemarin, 16:40",
            category = NotificationCategory.PANTRY,
            isRead = true,
            actionLabel = "Periksa Inventaris"
        ),
        AppNotification(
            id = "notif-4",
            title = "Catatan Masak Soto Lamongan Telah Tersimpan",
            message = "Evaluasi rasa: 'Kuah pas, taburan koya sangat renyah'. Foto dan catatan telah diabadikan di Jurnal Cerita Rasa.",
            timeLabel = "2 hari lalu",
            category = NotificationCategory.TIPS,
            isRead = true,
            actionLabel = "Lihat Jurnal"
        )
    )

    // StateFlow streams
    private val _recipes = MutableStateFlow(initialRecipes)
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    fun addRecipe(recipe: Recipe) {
        _recipes.update { listOf(recipe) + it }
        _collections.update { list ->
            list.map { col ->
                if (col.title.equals(recipe.category, ignoreCase = true) && !col.recipeIds.contains(recipe.id)) {
                    col.copy(
                        recipeIds = col.recipeIds + recipe.id,
                        recipeCount = col.recipeCount + 1
                    )
                } else col
            }
        }
    }

    fun updateRecipe(updatedRecipe: Recipe) {
        _recipes.update { list ->
            list.map { if (it.id == updatedRecipe.id) updatedRecipe else it }
        }
        _collections.update { list ->
            list.map { col ->
                val shouldContain = col.title.equals(updatedRecipe.category, ignoreCase = true)
                val currentlyContains = col.recipeIds.contains(updatedRecipe.id)
                if (shouldContain && !currentlyContains) {
                    col.copy(recipeIds = col.recipeIds + updatedRecipe.id, recipeCount = col.recipeCount + 1)
                } else if (!shouldContain && currentlyContains) {
                    col.copy(recipeIds = col.recipeIds - updatedRecipe.id, recipeCount = maxOf(0, col.recipeCount - 1))
                } else col
            }
        }
    }

    private val _collections = MutableStateFlow(initialCollections)
    val collections: StateFlow<List<RecipeCollection>> = _collections.asStateFlow()

    private val initialStorageLocations = listOf(
        StorageLocation.KULKAS,
        StorageLocation.FREEZER,
        StorageLocation.PANTRI
    )
    private val _storageLocations = MutableStateFlow(initialStorageLocations)
    val storageLocations: StateFlow<List<StorageLocation>> = _storageLocations.asStateFlow()

    private val _stockItems = MutableStateFlow(initialStockItems)
    val stockItems: StateFlow<List<StockItem>> = _stockItems.asStateFlow()

    private val _schedules = MutableStateFlow(initialSchedules)
    val schedules: StateFlow<List<MealSchedule>> = _schedules.asStateFlow()

    private val _shoppingItems = MutableStateFlow(initialShoppingItems)
    val shoppingItems: StateFlow<List<ShoppingItem>> = _shoppingItems.asStateFlow()

    private val _tasteStories = MutableStateFlow(initialTasteStories)
    val tasteStories: StateFlow<List<TasteStory>> = _tasteStories.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _notifications = MutableStateFlow(initialNotifications)
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    init {
        syncScheduledIngredientsWithInventoryAndShoppingList()
    }

    /**
     * Automatic synchronization system between Scheduled Recipes, Inventory Stock, and Shopping List.
     * Checks if stock for scheduled meals is missing or insufficient, automatically creates ShoppingItems
     * for missing amounts, and dynamically updates MealSchedule.isReadyInFridge.
     */
    fun syncScheduledIngredientsWithInventoryAndShoppingList() {
        val currentSchedules = _schedules.value
        val currentStock = _stockItems.value
        val currentShopping = _shoppingItems.value.toMutableList()
        var shoppingChanged = false

        val proteinRegex = Regex("daging|ayam|ikan|salmon|telur|fillet|sapi|udang", RegexOption.IGNORE_CASE)
        val bumbuRegex = Regex("bawang|cabai|bumbu|garam|gula|merica|kecap|saus|serai|kunyit|santan|jahe|koya|rempah", RegexOption.IGNORE_CASE)
        val sayurRegex = Regex("buncis|wortel|brokoli|kol|sayur|tomat|jeruk|pisang|kemangi|seledri|soun|labu|jagung|melinjo", RegexOption.IGNORE_CASE)

        val updatedSchedules = currentSchedules.map { schedule ->
            val scheduledRecipes = schedule.allRecipes
            if (scheduledRecipes.isEmpty()) {
                schedule.copy(isReadyInFridge = false)
            } else {
                var allIngredientsAvailable = true

                scheduledRecipes.forEach { recipe ->
                    recipe.ingredients.forEach { ing ->
                        val matchedStock = currentStock.find { stock ->
                            stock.name.contains(ing.name, ignoreCase = true) ||
                            ing.name.contains(stock.name, ignoreCase = true)
                        }

                        val availableQty = matchedStock?.quantity ?: 0.0
                        val isSufficient = availableQty >= ing.amount

                        if (!isSufficient) {
                            allIngredientsAvailable = false
                            val deficiency = maxOf(1.0, ing.amount - availableQty)

                            val existingItem = currentShopping.find { shop ->
                                !shop.isPurchased && (
                                    shop.name.equals(ing.name, ignoreCase = true) ||
                                    (shop.linkedRecipeTitle.equals(recipe.title, ignoreCase = true) && shop.name.contains(ing.name, ignoreCase = true))
                                )
                            }

                            if (existingItem == null && schedule.autoSyncGrocery) {
                                val cat = when {
                                    proteinRegex.containsMatchIn(ing.name) -> "protein"
                                    bumbuRegex.containsMatchIn(ing.name) -> "bumbu"
                                    sayurRegex.containsMatchIn(ing.name) -> "sayur"
                                    else -> "sembako"
                                }

                                val newShoppingItem = ShoppingItem(
                                    id = "shp-auto-${System.currentTimeMillis()}-${ing.name.hashCode()}",
                                    name = ing.name,
                                    quantity = deficiency,
                                    unit = ing.unit,
                                    category = cat,
                                    urgency = UrgencyLevel.URGENT,
                                    linkedRecipeTitle = recipe.title,
                                    estimatedPrice = com.example.menuku.core.utils.AiPriceEstimator.estimatePrice(ing.name, deficiency, ing.unit, cat),
                                    storeRecommendation = "Pasar Tradisional / Supermarket",
                                    notes = "Otomatis: Stok ${ing.name} kurang untuk jadwal ${schedule.slot.label} (${schedule.dayName}). Ada: $availableQty ${ing.unit}, butuh: ${ing.amount} ${ing.unit}.",
                                    isPurchased = false,
                                    isAutoGenerated = true
                                )
                                currentShopping.add(newShoppingItem)
                                shoppingChanged = true
                            }
                        }
                    }
                }

                schedule.copy(isReadyInFridge = allIngredientsAvailable)
            }
        }

        _schedules.value = updatedSchedules
        if (shoppingChanged) {
            _shoppingItems.value = currentShopping
        }
    }

    // Recipe functions
    fun toggleFavoriteRecipe(recipeId: String) {
        _recipes.update { list ->
            list.map { if (it.id == recipeId) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    fun addRecipeToCollection(collectionId: String, recipeId: String) {
        _collections.update { list ->
            list.map { col ->
                if (col.id == collectionId && !col.recipeIds.contains(recipeId)) {
                    col.copy(
                        recipeIds = col.recipeIds + recipeId,
                        recipeCount = col.recipeCount + 1
                    )
                } else col
            }
        }
    }

    fun createCollection(title: String, desc: String, icon: String = "eco", color: String = "hijau", recipeIds: List<String>) {
        val newCol = RecipeCollection(
            id = "col-${System.currentTimeMillis()}",
            title = title,
            description = desc,
            coverUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC8hVMk-NZOJCN-5oy0Fxbr9LdAGB5XEcqo5-PuEkbIjJ3cdKfGabTPNVqiDz2pPfscfxpvjV671BVtFa7ju98qkkT0atDR-lN0mAOD5tNZltBtLoeIVFpD4KirzDkEdqBaktlZc2NBXEUtWJb6PTZ0_u4FxfSq_7yrlLeDSJsw2SpFFy-DqzSO9ty8qQjnwuBtAl24Hu-aYRDEfizjLEvS2JPzhY609kePO1nw7f9f",
            recipeCount = recipeIds.size,

            difficulty = "Bervariasi",
            authorNote = "Koleksi khusus pilihan dapur keluarga.",
            iconName = icon,
            themeColor = color,
            recipeIds = recipeIds
        )
        _collections.update { it + newCol }
    }

    fun updateCollection(updated: RecipeCollection) {
        _collections.update { list ->
            list.map { if (it.id == updated.id) updated.copy(recipeCount = updated.recipeIds.size) else it }
        }
    }

    fun deleteCollection(collectionId: String) {
        _collections.update { list ->
            list.filterNot { it.id == collectionId }
        }
    }

    // Stock functions
    fun addStockItem(item: StockItem) {
        _stockItems.update { it + item }
        syncScheduledIngredientsWithInventoryAndShoppingList()
    }

    fun updateStockQuantity(id: String, newQty: Double) {
        _stockItems.update { list ->
            list.map { if (it.id == id) it.copy(quantity = maxOf(0.0, newQty)) else it }
                .filter { it.quantity > 0.0 }
        }
        syncScheduledIngredientsWithInventoryAndShoppingList()
    }

    fun moveStockToLocation(id: String, newLocation: StorageLocation) {
        _stockItems.update { list ->
            list.map { if (it.id == id) it.copy(location = newLocation) else it }
        }
        syncScheduledIngredientsWithInventoryAndShoppingList()
    }

    fun deleteStockItem(id: String) {
        _stockItems.update { list -> list.filterNot { it.id == id } }
        syncScheduledIngredientsWithInventoryAndShoppingList()
    }

    fun addStorageLocation(label: String, tempDesc: String) {
        val newLoc = StorageLocation(
            id = "loc-${System.currentTimeMillis()}",
            label = label,
            tempDesc = tempDesc
        )
        _storageLocations.update { it + newLoc }
    }

    fun updateStockItem(item: StockItem) {
        _stockItems.update { list ->
            list.map { if (it.id == item.id) item else it }
        }
        syncScheduledIngredientsWithInventoryAndShoppingList()
    }

    // Schedule functions
    fun addOrUpdateSchedule(schedule: MealSchedule) {
        _schedules.update { list ->
            val existing = list.find { it.dateStr == schedule.dateStr && it.slot == schedule.slot }
            if (existing != null) {
                list.map { if (it.id == existing.id) schedule else it }
            } else {
                list + schedule
            }
        }
        syncScheduledIngredientsWithInventoryAndShoppingList()
    }

    // Shopping functions
    fun addShoppingItem(item: ShoppingItem) {
        _shoppingItems.update { it + item }
    }

    fun toggleShoppingItemPurchased(id: String) {
        _shoppingItems.update { list ->
            list.map { item ->
                if (item.id == id) {
                    val updatedStatus = !item.isPurchased
                    if (updatedStatus && _userProfile.value.syncShoppingToPantry) {
                        _stockItems.update { stocks ->
                            val existingStock = stocks.find { it.name.contains(item.name, ignoreCase = true) || item.name.contains(it.name, ignoreCase = true) }
                            if (existingStock != null) {
                                stocks.map { if (it.id == existingStock.id) it.copy(quantity = it.quantity + item.quantity) else it }
                            } else {
                                stocks + StockItem(
                                    id = "stk-sync-${System.currentTimeMillis()}",
                                    name = item.name,
                                    quantity = item.quantity,
                                    unit = item.unit,
                                    location = StorageLocation.KULKAS,
                                    category = when (item.category) {
                                        "sayur" -> "Sayur & Buah"
                                        "protein" -> "Daging & Telur"
                                        "bumbu" -> "Bumbu Rempah"
                                        else -> "Sembako"
                                    },
                                    purchaseDate = "Hari Ini",
                                    expiryDate = "7 Hari Lagi",
                                    remainingDays = 7,
                                    note = "Otomatis disinkronisasi dari Daftar Belanja."
                                )
                            }
                        }
                    }
                    item.copy(isPurchased = updatedStatus)
                } else item
            }
        }
        syncScheduledIngredientsWithInventoryAndShoppingList()
    }

    fun updateShoppingItemQty(id: String, delta: Double) {
        _shoppingItems.update { list ->
            list.map {
                if (it.id == id) it.copy(quantity = maxOf(10.0, it.quantity + delta)) else it
            }
        }
    }

    fun deleteShoppingItem(id: String) {
        _shoppingItems.update { list -> list.filterNot { it.id == id } }
    }

    // Journal functions
    fun addTasteStory(story: TasteStory) {
        _tasteStories.update { listOf(story) + it }
    }

    fun deleteTasteStory(id: String) {
        _tasteStories.update { list -> list.filterNot { it.id == id } }
    }

    // Profile & Preferences functions
    fun updateProfile(updater: (UserProfile) -> UserProfile) {
        _userProfile.update(updater)
    }

    // Notification functions
    fun markAllNotificationsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    // Firebase Auth & Firestore integration methods
    suspend fun loginWithEmail(email: String, pass: String): Result<Unit> {
        return try {
            val authResult = FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, pass)
                .await()
            val uid = authResult.user?.uid
            if (uid != null) {
                val doc = FirebaseFirestore.getInstance()
                    .collection("users").document(uid).get().await()
                if (doc.exists()) {
                    val name = doc.getString("name") ?: "Pengguna MenuKu"
                    val userEmail = doc.getString("email") ?: email
                    _userProfile.update { it.copy(name = name, email = userEmail) }
                } else {
                    _userProfile.update { it.copy(email = email) }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(name: String, email: String, pass: String, phone: String): Result<Unit> {
        return try {
            val authResult = FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, pass)
                .await()
            val uid = authResult.user?.uid
            if (uid != null) {
                val userMap = mapOf(
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "createdAt" to Timestamp.now()
                )
                FirebaseFirestore.getInstance()
                    .collection("users").document(uid).set(userMap).await()

                _userProfile.update { it.copy(name = name, email = email) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogleToken(idToken: String): Result<Unit> {
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) {
                val uid = user.uid
                val name = user.displayName ?: "Pengguna Google"
                val email = user.email ?: ""
                val photoUrl = user.photoUrl?.toString() ?: ""

                val userMap = mapOf(
                    "name" to name,
                    "email" to email,
                    "createdAt" to Timestamp.now()
                )
                FirebaseFirestore.getInstance()
                    .collection("users").document(uid).set(userMap, com.google.firebase.firestore.SetOptions.merge()).await()

                _userProfile.update { it.copy(name = name, email = email, avatarUrl = if (photoUrl.isNotBlank()) photoUrl else it.avatarUrl) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result)
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        addOnCanceledListener {
            continuation.cancel()
        }
    }
}
