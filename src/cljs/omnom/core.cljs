(ns omnom.core
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [goog.dom :as gdom]))

(def app-state
  (atom
   {:notes [{:x 100
             :y 50
             :w 200
             :content "# This is cool\nTite *tite* __tite__\n```js\nfunction add(a, b) {\n  return a + b;\n}\n```"}
            {:x 200
             :y 300
             :w 400
             :content "[Gooooogle](http://google.com)"}
            {:x 500
             :y 20
             :w 200
             :content "# Head\n## lines\n### are cool"}]}))

(defn on-next-tick
  [fn]
  (js/setTimeout fn 0)
  fn)

(defn highlight!
  [node]
  (on-next-tick
   #(let [pre-blocks (array-seq (gdom/getElementsByTagNameAndClass "pre" nil node))]
     (doall
      (map (fn [pre-block]
             (.highlightBlock js/hljs pre-block))
           pre-blocks)))))

(defcomponent note-view
  [note owner]

  (init-state
   [_]
   {:mounted false})

  (did-mount
   [_]
   (om/set-state! owner :mounted true))

  (render
   [_]
   (let [{:keys [x y w h content]} note]
     (when (om/get-state owner :mounted)
       (highlight! (om/get-node owner)))
     (dom/div {:class "note"
               :style {:left x :top y :width w}
               :dangerouslySetInnerHTML {:__html (js/marked content)}
               :on-click (fn []
                           (om/transact! note :content
                                         #(str % "\n```javascript\nfunction meta-random() {\n  return " (rand-int 2048) ";\n}\n```")))}))))

(defcomponent root-view
  [app owner]

  (render
   [_]
   (dom/div
    (om/build-all note-view (:notes app)))))

(defn main []
  (om/root
    root-view
    app-state
    {:target (. js/document (getElementById "app"))}))
