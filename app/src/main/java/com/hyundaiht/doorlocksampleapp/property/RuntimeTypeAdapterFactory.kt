import android.util.Log
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class RuntimeTypeAdapterFactory<T>(
    private val baseType: Class<T>,
    private val typeFieldName: String
) : TypeAdapterFactory {
    private val tag = javaClass.simpleName
    private val labelToSubtype: MutableMap<String, Class<out T>> = mutableMapOf()
    private val subtypeToLabel: MutableMap<Class<out T>, String> = mutableMapOf()

    fun registerSubtype(subtype: Class<out T>, label: String): RuntimeTypeAdapterFactory<T> {
        labelToSubtype[label] = subtype
        subtypeToLabel[subtype] = label
        return this
    }

    override fun <R : Any> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
        Log.d(tag, "create baseType = $baseType, name = ${baseType.simpleName}")
        Log.d(tag, "create typeFieldName = $typeFieldName")
        Log.d(tag, "create isAssignableFrom = ${baseType.isAssignableFrom(type.rawType)}")
        if (!baseType.isAssignableFrom(type.rawType)) {
            return null
        }

        val labelToAdapter: MutableMap<String, TypeAdapter<T>> = mutableMapOf()
        val subtypeToAdapter: MutableMap<Class<out T>, TypeAdapter<T>> = mutableMapOf()

        labelToSubtype.forEach { (label, subtype) ->
            Log.d(tag, "----------------------------------- labelToSubtype ---------------------------------")
            Log.d(tag, "label = $label, subtype = $subtype")
            val adapter = gson.getDelegateAdapter(this, TypeToken.get(subtype)) as TypeAdapter<T>
            labelToAdapter[label] = adapter
            subtypeToAdapter[subtype] = adapter
            Log.d(tag, "--------------------------------------------------------------------")
        }

        return object : TypeAdapter<R>() {
            override fun write(out: JsonWriter, value: R) {
                Log.d(tag, "------------------------------- TypeAdapter write -------------------------------------")
                val clazz = value::class.java as Class<out T>
                val label = subtypeToLabel[clazz]
                    ?: throw JsonParseException("Unknown type: ${clazz.name}")

                @Suppress("UNCHECKED_CAST")
                val delegate = subtypeToAdapter[clazz] as? TypeAdapter<R>
                    ?: throw JsonParseException("No adapter for type: ${clazz.name}")
                Log.d(tag, "label = $label, delegate = $delegate, typeFieldName = $typeFieldName")

                val jsonObject = delegate.toJsonTree(value).asJsonObject
                jsonObject.addProperty(typeFieldName, label)
                gson.toJson(jsonObject, out)
                Log.d(tag, "jsonObject = $jsonObject")
                Log.d(tag, "---------------------------------------------------------------------------------------")
            }

            override fun read(`in`: JsonReader): R {
                Log.d(tag, "------------------------------- TypeAdapter read -------------------------------------")
                val jsonObject = JsonParser.parseReader(`in`).asJsonObject
                Log.d(tag, "jsonObject = $jsonObject")
                val label = jsonObject.get(typeFieldName).asString
                val delegate = labelToAdapter[label]
                    ?: throw JsonParseException("Unknown type: $label")
                Log.d(tag, "label = $label, delegate = $delegate, typeFieldName = $typeFieldName")
                Log.d(tag, "---------------------------------------------------------------------------------------")
                @Suppress("UNCHECKED_CAST")
                return delegate.fromJsonTree(jsonObject) as R
            }
        }.nullSafe()
    }

    companion object {
        fun <T> of(baseType: Class<T>, typeFieldName: String): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, typeFieldName)
        }
    }
}
